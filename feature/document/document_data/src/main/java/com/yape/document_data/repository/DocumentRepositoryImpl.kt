package com.yape.document_data.repository

import android.content.Context
import android.net.Uri
import com.yape.common.helper.SecureFileFacade.Companion.queryMetadata
import com.yape.document_data.local.storage.DocumentLocalStorage
import com.yape.document_data.mapper.documentEntityToDomain
import com.yape.document_domain.model.DocumentDomain
import com.yape.document_domain.model.DocumentType
import com.yape.document_domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DocumentRepositoryImpl(
    private val context: Context,
    private val documentLocalStorage: DocumentLocalStorage
) : DocumentRepository {

    override fun getDocuments(): Flow<List<DocumentDomain>> {
        return documentLocalStorage.getDocuments().map { entities ->
            entities.map { it.documentEntityToDomain() }
        }
    }

    override suspend fun addDocument(uri: Uri, type: DocumentType): DocumentDomain {
        val (name, size) = uri.queryMetadata(context)
        val entity = documentLocalStorage.addDocument(uri, name, type.name, size)
        if (entity != null) return entity.documentEntityToDomain()

        // Duplicate: return the existing document with the same name
        return getDocuments().first().find { it.name == name }
            ?: error("Documento duplicado mas não encontrado")
    }

    override suspend fun deleteDocument(id: String) {
        documentLocalStorage.deleteDocument(id)
    }

    override suspend fun recordAccess(id: String) {
        documentLocalStorage.recordAccess(id)
    }
}
