package com.yape.document_domain.usecase

import android.net.Uri
import com.yape.document_domain.model.DocumentDomain
import com.yape.document_domain.model.DocumentType
import com.yape.document_domain.repository.DocumentRepository

class AddDocumentUseCase(private val repository: DocumentRepository) {
    suspend operator fun invoke(uri: Uri, type: DocumentType): DocumentDomain =
        repository.addDocument(uri, type)
}
