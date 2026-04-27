package com.yape.document_domain.usecase

import android.net.Uri
import com.yape.document_domain.model.DocumentDomain
import com.yape.document_domain.model.DocumentType
import com.yape.document_domain.repository.DocumentRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AddDocumentUseCaseTest {

    private val repository = mockk<DocumentRepository>()
    private lateinit var useCase: AddDocumentUseCase

    @Before
    fun setUp() {
        useCase = AddDocumentUseCase(repository)
    }

    @Test
    fun given_repository_when_invoked_then_delegates_to_repository() = runTest {
        val mockUri = mockk<Uri>()
        val expected = DocumentDomain("1", "doc.pdf", DocumentType.PDF, "path", 1_000L, 512L, persistentListOf())
        coEvery { repository.addDocument(mockUri, DocumentType.PDF) } returns expected

        val result = useCase(mockUri, DocumentType.PDF)

        assertEquals(expected, result)
        coVerify(exactly = 1) { repository.addDocument(mockUri, DocumentType.PDF) }
    }
}
