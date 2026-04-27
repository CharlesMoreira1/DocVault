package com.yape.document_data.repository

import android.content.Context
import android.net.Uri
import app.cash.turbine.test
import com.yape.common.helper.SecureFileFacade
import com.yape.common.helper.SecureFileFacade.Companion.queryMetadata
import com.yape.document_data.local.storage.DocumentLocalStorage
import com.yape.document_data.model.DocumentEntity
import com.yape.document_domain.model.DocumentType
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DocumentRepositoryImplTest {

    private val context = mockk<Context>()
    private val documentLocalStorage = mockk<DocumentLocalStorage>()
    private lateinit var repository: DocumentRepositoryImpl

    @Before
    fun setUp() {
        mockkObject(SecureFileFacade.Companion)
        repository = DocumentRepositoryImpl(context, documentLocalStorage)
    }

    @After
    fun tearDown() {
        unmockkObject(SecureFileFacade.Companion)
    }

    @Test
    fun given_storage_returns_entities_when_get_documents_then_returns_mapped_domain_list() = runTest {
        val entity = DocumentEntity("id1", "doc.pdf", "PDF", "path/doc.pdf", 1_000L, 512L)
        every { documentLocalStorage.getDocuments() } returns flowOf(listOf(entity))

        repository.getDocuments().test {
            val domains = awaitItem()
            assertEquals(1, domains.size)
            assertEquals("id1", domains[0].id)
            assertEquals("doc.pdf", domains[0].name)
            assertEquals(DocumentType.PDF, domains[0].type)
            awaitComplete()
        }
    }

    @Test
    fun given_non_duplicate_uri_when_add_document_then_returns_new_entity_mapped() = runTest {
        val mockUri = mockk<Uri>()
        val entity = DocumentEntity("id1", "doc.pdf", "PDF", "path/doc.pdf", 1_000L, 512L)
        every { mockUri.queryMetadata(any()) } returns Pair("doc.pdf", 512L)
        coEvery { documentLocalStorage.addDocument(mockUri, "doc.pdf", "PDF", 512L) } returns entity

        val result = repository.addDocument(mockUri, DocumentType.PDF)

        assertEquals("id1", result.id)
        assertEquals("doc.pdf", result.name)
        assertEquals(DocumentType.PDF, result.type)
    }

    @Test
    fun given_duplicate_name_when_add_document_then_returns_existing_document() = runTest {
        val mockUri = mockk<Uri>()
        val existingEntity = DocumentEntity("existing_id", "doc.pdf", "PDF", "path", 1_000L, 512L)
        every { mockUri.queryMetadata(any()) } returns Pair("doc.pdf", 512L)
        coEvery { documentLocalStorage.addDocument(any(), "doc.pdf", "PDF", 512L) } returns null
        every { documentLocalStorage.getDocuments() } returns flowOf(listOf(existingEntity))

        val result = repository.addDocument(mockUri, DocumentType.PDF)

        assertEquals("existing_id", result.id)
    }

    @Test
    fun given_id_when_delete_document_then_delegates_to_storage() = runTest {
        coJustRun { documentLocalStorage.deleteDocument("id1") }

        repository.deleteDocument("id1")

        coVerify(exactly = 1) { documentLocalStorage.deleteDocument("id1") }
    }
}
