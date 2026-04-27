@file:OptIn(ExperimentalCoroutinesApi::class)

package com.yape.detail_presentation

import androidx.compose.ui.graphics.ImageBitmap
import com.yape.common.helper.DocumentFileInputStreamHelper
import com.yape.document_domain.model.DocumentDomain
import com.yape.document_domain.model.DocumentType
import com.yape.document_domain.usecase.GetDocumentsUseCase
import com.yape.document_domain.usecase.RecordAccessUseCase
import io.mockk.Runs
import io.mockk.coJustRun
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DocumentDetailViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val getDocumentsUseCase = mockk<GetDocumentsUseCase>()
    private val recordAccessUseCase = mockk<RecordAccessUseCase>()
    private val documentFileInputStreamHelper = mockk<DocumentFileInputStreamHelper>()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(documentId: String = "id1") = DocumentDetailViewModel(
        getDocumentsUseCase = getDocumentsUseCase,
        recordAccessUseCase = recordAccessUseCase,
        documentFileInputStreamHelper = documentFileInputStreamHelper,
        documentId = documentId,
        coroutineDispatcher = testDispatcher
    )

    @Test
    fun given_document_not_in_list_when_initialized_then_state_is_error() = runTest(testDispatcher) {
        every { getDocumentsUseCase() } returns flowOf(emptyList())

        val vm = createViewModel()

        assertTrue(vm.uiState.value.content is DocumentContent.Error)
    }

    @Test
    fun given_image_document_when_initialized_then_records_access_and_loads_image() = runTest(testDispatcher) {
        val doc = DocumentDomain("id1", "photo.jpg", DocumentType.IMAGE, "path/photo.jpg", 0L, 0L)
        val mockBitmap = mockk<ImageBitmap>()
        every { getDocumentsUseCase() } returns flowOf(listOf(doc))
        coJustRun { recordAccessUseCase(any()) }
        every { documentFileInputStreamHelper.getFileImage(any()) } returns mockBitmap

        val vm = createViewModel()

        assertTrue(vm.uiState.value.content is DocumentContent.Image)
        verify { documentFileInputStreamHelper.getFileImage("path/photo.jpg") }
    }

    @Test
    fun given_pdf_document_when_initialized_then_records_access_and_loads_pdf() = runTest(testDispatcher) {
        val doc = DocumentDomain("id1", "doc.pdf", DocumentType.PDF, "path/doc.pdf", 0L, 0L)
        val mockPage = mockk<ImageBitmap>()
        every { getDocumentsUseCase() } returns flowOf(listOf(doc))
        coJustRun { recordAccessUseCase(any()) }
        every { documentFileInputStreamHelper.getFilePDF(any(), any()) } returns listOf(mockPage)
        every { documentFileInputStreamHelper.deleteFilePDF(any()) } just Runs

        val vm = createViewModel()

        assertTrue(vm.uiState.value.content is DocumentContent.Pdf)
        verify { documentFileInputStreamHelper.getFilePDF("path/doc.pdf", "id1") }
    }

    @Test
    fun given_image_load_throws_when_loading_then_state_is_error() = runTest(testDispatcher) {
        val doc = DocumentDomain("id1", "photo.jpg", DocumentType.IMAGE, "path/photo.jpg", 0L, 0L)
        every { getDocumentsUseCase() } returns flowOf(listOf(doc))
        coJustRun { recordAccessUseCase(any()) }
        every { documentFileInputStreamHelper.getFileImage(any()) } throws RuntimeException("IO error")

        val vm = createViewModel()

        assertTrue(vm.uiState.value.content is DocumentContent.Error)
    }

    @Test
    fun given_pdf_load_throws_when_loading_then_state_is_error_and_temp_file_deleted() = runTest(testDispatcher) {
        val doc = DocumentDomain("id1", "doc.pdf", DocumentType.PDF, "path/doc.pdf", 0L, 0L)
        every { getDocumentsUseCase() } returns flowOf(listOf(doc))
        coJustRun { recordAccessUseCase(any()) }
        every { documentFileInputStreamHelper.getFilePDF(any(), any()) } throws RuntimeException("PDF error")
        every { documentFileInputStreamHelper.deleteFilePDF(any()) } just Runs

        val vm = createViewModel()

        assertTrue(vm.uiState.value.content is DocumentContent.Error)
        verify { documentFileInputStreamHelper.deleteFilePDF("id1") }
    }
}
