package com.yape.document_domain.repository

import android.net.Uri
import com.yape.document_domain.model.DocumentDomain
import com.yape.document_domain.model.DocumentType
import kotlinx.coroutines.flow.Flow

interface DocumentRepository {
    fun getDocuments(): Flow<List<DocumentDomain>>
    suspend fun addDocument(uri: Uri, type: DocumentType): DocumentDomain
    suspend fun deleteDocument(id: String)
    suspend fun recordAccess(id: String)
}
