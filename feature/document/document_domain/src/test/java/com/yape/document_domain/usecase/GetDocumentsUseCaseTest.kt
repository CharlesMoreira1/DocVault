package com.yape.document_domain.usecase

import app.cash.turbine.test
import com.yape.document_domain.model.DocumentDomain
import com.yape.document_domain.model.DocumentType
import com.yape.document_domain.repository.DocumentRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetDocumentsUseCaseTest {

    private val repository = mockk<DocumentRepository>()
    private lateinit var useCase: GetDocumentsUseCase

    @Before
    fun setUp() {
        useCase = GetDocumentsUseCase(repository)
    }

    @Test
    fun given_repository_with_documents_when_invoked_then_returns_documents_flow() = runTest {
        val documents = listOf(
            DocumentDomain("1", "doc.pdf", DocumentType.PDF, "path/doc.pdf", 1_000L, 512L, persistentListOf())
        )
        every { repository.getDocuments() } returns flowOf(documents)

        useCase().test {
            assertEquals(documents, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun given_repository_with_empty_list_when_invoked_then_returns_empty_flow() = runTest {
        every { repository.getDocuments() } returns flowOf(emptyList())

        useCase().test {
            assertEquals(emptyList<DocumentDomain>(), awaitItem())
            awaitComplete()
        }
    }
}
