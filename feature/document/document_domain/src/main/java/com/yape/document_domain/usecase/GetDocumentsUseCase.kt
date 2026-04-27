package com.yape.document_domain.usecase

import com.yape.document_domain.model.DocumentDomain
import com.yape.document_domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow

class GetDocumentsUseCase(private val repository: DocumentRepository) {
    operator fun invoke(): Flow<List<DocumentDomain>> = repository.getDocuments()
}
