package com.yape.document_domain.usecase

import com.yape.document_domain.repository.DocumentRepository
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DeleteDocumentUseCaseTest {

    private val repository = mockk<DocumentRepository>()
    private lateinit var useCase: DeleteDocumentUseCase

    @Before
    fun setUp() {
        useCase = DeleteDocumentUseCase(repository)
    }

    @Test
    fun given_repository_when_invoked_with_id_then_delegates_to_repository() = runTest {
        coJustRun { repository.deleteDocument("id1") }

        useCase("id1")

        coVerify(exactly = 1) { repository.deleteDocument("id1") }
    }
}
