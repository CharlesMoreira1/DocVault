package com.yape.document_domain.usecase

import com.yape.document_domain.repository.DocumentRepository

class DeleteDocumentUseCase(private val repository: DocumentRepository) {
    suspend operator fun invoke(id: String) = repository.deleteDocument(id)
}
