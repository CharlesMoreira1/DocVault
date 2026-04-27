package com.yape.document_domain.usecase

import com.yape.document_domain.repository.DocumentRepository
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class RecordAccessUseCaseTest {

    private val repository = mockk<DocumentRepository>()
    private lateinit var useCase: RecordAccessUseCase

    @Before
    fun setUp() {
        useCase = RecordAccessUseCase(repository)
    }

    @Test
    fun given_repository_when_invoked_with_id_then_delegates_to_repository() = runTest {
        coJustRun { repository.recordAccess("id1") }

        useCase("id1")

        coVerify(exactly = 1) { repository.recordAccess("id1") }
    }
}
