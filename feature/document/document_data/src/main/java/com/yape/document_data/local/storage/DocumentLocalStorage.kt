package com.yape.document_data.local.storage

import android.content.Context
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.yape.common.helper.SecureFileFacade
import com.yape.document_data.model.DocumentEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import timber.log.Timber

class DocumentLocalStorage(
    private val context: Context,
    private val secureFileFacade: SecureFileFacade
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_KEY_NAME)
    private val documentsKey = stringPreferencesKey(DOCUMENTS_KEY_NAME)

    fun getDocuments(): Flow<List<DocumentEntity>> =
        context.dataStore.data.map { prefs ->
            val json = prefs[documentsKey] ?: return@map emptyList()
            runCatching { Json.decodeFromString<List<DocumentEntity>>(json) }
                .onFailure { Timber.e(it, "Failed to deserialize documents") }
                .getOrDefault(emptyList())
        }

    suspend fun addDocument(uri: Uri, fileName: String, type: String, sizeBytes: Long): DocumentEntity? {
        var result: DocumentEntity? = null
        context.dataStore.edit { prefs ->
            val current = decode(prefs)
            if (current.any { it.name == fileName }) return@edit

            val id = UUID.randomUUID().toString()
            val relativePath = secureFileFacade.saveFile(uri, "${id}_$fileName")
            val entity = DocumentEntity(
                id = id,
                name = fileName,
                type = type,
                relativePath = relativePath,
                createdAt = System.currentTimeMillis(),
                sizeBytes = sizeBytes
            )
            prefs[documentsKey] = Json.encodeToString(current + entity)
            result = entity
        }
        return result
    }

    suspend fun deleteDocument(id: String) {
        context.dataStore.edit { prefs ->
            val current = decode(prefs)
            current.find { it.id == id }?.let { secureFileFacade.deleteFile(it.relativePath) }
            prefs[documentsKey] = Json.encodeToString(current.filter { it.id != id })
        }
    }

    suspend fun recordAccess(id: String) {
        context.dataStore.edit { prefs ->
            val current = decode(prefs)
            val updated = current.map { entity ->
                if (entity.id == id) {
                    val newLog = (entity.accessLog + System.currentTimeMillis()).takeLast(MAX_ACCESS_LOG)
                    entity.copy(accessLog = newLog)
                } else entity
            }
            prefs[documentsKey] = Json.encodeToString(updated)
        }
    }

    private fun decode(prefs: Preferences): List<DocumentEntity> =
        prefs[documentsKey]
            ?.let {
                runCatching { Json.decodeFromString<List<DocumentEntity>>(it) }
                    .onFailure { e -> Timber.e(e, "Failed to deserialize documents in decode") }
                    .getOrNull()
            }
            ?: emptyList()

    companion object {
        private const val DATA_STORE_KEY_NAME = "docvault"
        private const val DOCUMENTS_KEY_NAME = "documents_list"
        private const val MAX_ACCESS_LOG = 10
    }
}
