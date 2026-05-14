package com.yape.document_data.local.storage

import android.content.Context
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import app.cash.turbine.test
import com.yape.common.helper.SecureFileFacade
import com.yape.document_data.model.DocumentEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import kotlin.properties.ReadOnlyProperty

class DocumentLocalStorageTest {

    private val context = mockk<Context>()
    private val secureFileFacade = mockk<SecureFileFacade>()
    private val mockDataStore = mockk<DataStore<Preferences>>()
    private val documentsKey = stringPreferencesKey("documents_list")
    private lateinit var storage: DocumentLocalStorage

    @Before
    fun setUp() {
        storage = DocumentLocalStorage(context, secureFileFacade)
        val delegateField = DocumentLocalStorage::class.java.getDeclaredField("dataStore\$delegate")
        delegateField.isAccessible = true
        delegateField.set(storage, ReadOnlyProperty<Context, DataStore<Preferences>> { _, _ -> mockDataStore })
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    private fun stubUpdateData(
        initialJson: String?,
        onResult: ((String?) -> Unit)? = null
    ) {
        coEvery { mockDataStore.updateData(any()) } coAnswers {
            @Suppress("UNCHECKED_CAST")
            val transform = firstArg<suspend (Preferences) -> Preferences>()
            val initialPrefs = if (initialJson != null)
                mutablePreferencesOf(documentsKey to initialJson)
            else
                mutablePreferencesOf()
            val resultPrefs = transform(initialPrefs)
            onResult?.invoke(resultPrefs[documentsKey])
            resultPrefs
        }
    }

    private fun jsonOf(vararg entities: DocumentEntity) = Json.encodeToString(entities.toList())

    private fun entity(
        id: String = "id1",
        name: String = "file.pdf",
        type: String = "PDF",
        path: String = "documents/file.pdf",
        createdAt: Long = 1_000L,
        size: Long = 512L,
        accessLog: List<Long> = emptyList()
    ) = DocumentEntity(id, name, type, path, createdAt, size, accessLog)

    @Test
    fun given_valid_json_in_prefs_when_get_documents_then_returns_deserialized_list() = runTest {
        val e = entity()
        every { mockDataStore.data } returns flowOf(mutablePreferencesOf(documentsKey to jsonOf(e)))

        storage.getDocuments().test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals(e.id, result[0].id)
            assertEquals(e.name, result[0].name)
            awaitComplete()
        }
    }

    @Test
    fun given_no_entry_in_prefs_when_get_documents_then_returns_empty_list() = runTest {
        every { mockDataStore.data } returns flowOf(mutablePreferencesOf())

        storage.getDocuments().test {
            assertEquals(emptyList<DocumentEntity>(), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun given_malformed_json_in_prefs_when_get_documents_then_returns_empty_list() = runTest {
        every { mockDataStore.data } returns flowOf(mutablePreferencesOf(documentsKey to "not-valid{{"))

        storage.getDocuments().test {
            assertEquals(emptyList<DocumentEntity>(), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun given_no_duplicate_when_add_document_then_saves_file_and_returns_entity() = runTest {
        val uri = mockk<Uri>()
        stubUpdateData(initialJson = null)
        coEvery { secureFileFacade.saveFile(uri, any()) } returns "documents/id_file.pdf"

        val result = storage.addDocument(uri, "file.pdf", "PDF", 1024L)

        assertNotNull(result)
        assertEquals("file.pdf", result!!.name)
        assertEquals("PDF", result.type)
        assertEquals(1024L, result.sizeBytes)
        assertEquals("documents/id_file.pdf", result.relativePath)
        coVerify(exactly = 1) { secureFileFacade.saveFile(uri, any()) }
    }

    @Test
    fun given_duplicate_name_when_add_document_then_returns_null_without_saving() = runTest {
        val uri = mockk<Uri>()
        stubUpdateData(initialJson = jsonOf(entity(name = "file.pdf")))

        val result = storage.addDocument(uri, "file.pdf", "PDF", 1024L)

        assertNull(result)
        coVerify(exactly = 0) { secureFileFacade.saveFile(any(), any()) }
    }

    @Test
    fun given_existing_id_when_delete_document_then_deletes_encrypted_file() = runTest {
        val e = entity(id = "id1", path = "documents/file.pdf")
        stubUpdateData(initialJson = jsonOf(e))
        every { secureFileFacade.deleteFile("documents/file.pdf") } returns true

        storage.deleteDocument("id1")

        verify(exactly = 1) { secureFileFacade.deleteFile("documents/file.pdf") }
    }

    @Test
    fun given_non_existing_id_when_delete_document_then_does_not_delete_any_file() = runTest {
        stubUpdateData(initialJson = jsonOf(entity(id = "id1")))

        storage.deleteDocument("non-existing-id")

        verify(exactly = 0) { secureFileFacade.deleteFile(any()) }
    }

    @Test
    fun given_existing_id_when_record_access_then_access_log_has_one_entry() = runTest {
        val e = entity(id = "id1")
        var capturedJson: String? = null
        stubUpdateData(initialJson = jsonOf(e), onResult = { capturedJson = it })

        storage.recordAccess("id1")

        val updated = Json.decodeFromString<List<DocumentEntity>>(capturedJson!!)
        assertEquals(1, updated[0].accessLog.size)
    }

    @Test
    fun given_access_log_full_when_record_access_then_oldest_entry_is_dropped() = runTest {
        val fullLog = (1L..10L).toList()
        val e = entity(id = "id1", accessLog = fullLog)
        var capturedJson: String? = null
        stubUpdateData(initialJson = jsonOf(e), onResult = { capturedJson = it })

        storage.recordAccess("id1")

        val updated = Json.decodeFromString<List<DocumentEntity>>(capturedJson!!)
        assertEquals(10, updated[0].accessLog.size)
        assertEquals(false, updated[0].accessLog.contains(1L))
    }

    @Test
    fun given_non_existing_id_when_record_access_then_no_entity_is_modified() = runTest {
        val e = entity(id = "id1", accessLog = emptyList())
        var capturedJson: String? = null
        stubUpdateData(initialJson = jsonOf(e), onResult = { capturedJson = it })

        storage.recordAccess("non-existing-id")

        val updated = Json.decodeFromString<List<DocumentEntity>>(capturedJson!!)
        assertEquals(emptyList<Long>(), updated[0].accessLog)
    }
}
