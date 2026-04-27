@file:OptIn(ExperimentalCoroutinesApi::class)

package com.yape.home_presentation

import android.net.Uri
import app.cash.turbine.test
import com.yape.common.helper.ImageWatermarkHelper
import com.yape.document_domain.model.DocumentDomain
import com.yape.document_domain.model.DocumentFilter
import com.yape.document_domain.model.DocumentType
import com.yape.document_domain.usecase.AddDocumentUseCase
import com.yape.document_domain.usecase.DeleteDocumentUseCase
import com.yape.document_domain.usecase.GetDocumentsUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val getDocumentsUseCase = mockk<GetDocumentsUseCase>()
    private val addDocumentUseCase = mockk<AddDocumentUseCase>()
    private val deleteDocumentUseCase = mockk<DeleteDocumentUseCase>()
    private val imageWatermarkHelper = mockk<ImageWatermarkHelper>()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { getDocumentsUseCase() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = HomeViewModel(
        getDocumentsUseCase = getDocumentsUseCase,
        addDocumentUseCase = addDocumentUseCase,
        deleteDocumentUseCase = deleteDocumentUseCase,
        imageWatermarkHelper = imageWatermarkHelper,
        coroutineDispatcher = testDispatcher
    )

    @Test
    fun given_documents_in_repository_when_initialized_then_state_has_documents() = runTest(testDispatcher) {
        val doc = DocumentDomain("1", "doc.pdf", DocumentType.PDF, "path", 0L, 0L)
        every { getDocumentsUseCase() } returns flowOf(listOf(doc))

        val vm = createViewModel()

        assertEquals(listOf(doc).toImmutableList(), vm.uiState.value.documentDomains)
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun given_any_filter_when_filter_changed_event_then_state_updates_filter() = runTest(testDispatcher) {
        val vm = createViewModel()

        vm.handleEvent(HomeEvent.FilterChanged(DocumentFilter.IMAGE))

        assertEquals(DocumentFilter.IMAGE, vm.uiState.value.filter)
    }

    @Test
    fun given_fab_collapsed_when_fab_clicked_then_fab_is_expanded() = runTest(testDispatcher) {
        val vm = createViewModel()
        assertFalse(vm.uiState.value.isFabExpanded)

        vm.handleEvent(HomeEvent.FabClicked)

        assertTrue(vm.uiState.value.isFabExpanded)
    }

    @Test
    fun given_fab_expanded_when_fab_clicked_then_fab_is_collapsed() = runTest(testDispatcher) {
        val vm = createViewModel()
        vm.handleEvent(HomeEvent.FabClicked)

        vm.handleEvent(HomeEvent.FabClicked)

        assertFalse(vm.uiState.value.isFabExpanded)
    }

    @Test
    fun given_add_from_gallery_event_when_handled_then_emits_launch_gallery_effect() = runTest(testDispatcher) {
        val vm = createViewModel()

        vm.handleEvent(HomeEvent.AddFromGallery)

        vm.effects.test {
            assertTrue(awaitItem() is HomeEffect.LaunchGallery)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun given_document_clicked_event_when_handled_then_emits_navigate_to_detail_effect() = runTest(testDispatcher) {
        val vm = createViewModel()

        vm.handleEvent(HomeEvent.DocumentClicked("doc_id"))

        vm.effects.test {
            val effect = awaitItem()
            assertTrue(effect is HomeEffect.NavigateToDetail)
            assertEquals("doc_id", (effect as HomeEffect.NavigateToDetail).id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun given_add_document_event_when_use_case_throws_then_emits_show_error_effect() = runTest(testDispatcher) {
        val mockUri = mockk<Uri>()
        coEvery { addDocumentUseCase(any(), any()) } throws RuntimeException("save error")
        val vm = createViewModel()

        vm.handleEvent(HomeEvent.DocumentAdded(mockUri, DocumentType.PDF))

        vm.effects.test {
            assertTrue(awaitItem() is HomeEffect.ShowError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun given_remove_document_event_when_use_case_throws_then_emits_show_error_effect() = runTest(testDispatcher) {
        coEvery { deleteDocumentUseCase(any()) } throws RuntimeException("delete error")
        val vm = createViewModel()

        vm.handleEvent(HomeEvent.DocumentRemoveRequested("id1"))

        vm.effects.test {
            assertTrue(awaitItem() is HomeEffect.ShowError)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
