package com.yape.home_presentation

import androidx.lifecycle.ViewModel
import timber.log.Timber
import androidx.lifecycle.viewModelScope
import com.yape.common.R.string
import com.yape.common.helper.ImageWatermarkHelper
import com.yape.document_domain.usecase.AddDocumentUseCase
import com.yape.document_domain.usecase.DeleteDocumentUseCase
import com.yape.document_domain.usecase.GetDocumentsUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val getDocumentsUseCase: GetDocumentsUseCase,
    private val addDocumentUseCase: AddDocumentUseCase,
    private val deleteDocumentUseCase: DeleteDocumentUseCase,
    private val imageWatermarkHelper: ImageWatermarkHelper,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _effects = Channel<HomeEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        loadDocuments()
    }

    fun handleEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.FilterChanged -> {
                _uiState.update { it.copy(filter = event.filter) }
            }
            is HomeEvent.FabClicked -> {
                _uiState.update { it.copy(isFabExpanded = it.isFabExpanded.not()) }
            }
            is HomeEvent.AddFromGallery -> {
                _uiState.update { it.copy(isFabExpanded = false) }
                onEffect(HomeEffect.LaunchGallery)
            }
            is HomeEvent.AddFromCamera -> {
                _uiState.update { it.copy(isFabExpanded = false) }
                onEffect(HomeEffect.LaunchCamera)
            }
            is HomeEvent.DismissAddOptions -> {
                _uiState.update { it.copy(isFabExpanded = false) }
            }
            is HomeEvent.DocumentAdded -> {
                saveDocument(event)
            }
            is HomeEvent.DocumentClicked -> {
                onEffect(HomeEffect.NavigateToDetail(event.id))
            }
            is HomeEvent.DocumentRemoveRequested -> {
                removeDocument(event.id)
            }
        }
    }

    private fun loadDocuments() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            getDocumentsUseCase().collect { docs ->
                _uiState.update { it.copy(documentDomains = docs.toImmutableList(), isLoading = false) }
            }
        }
    }

    private fun saveDocument(event: HomeEvent.DocumentAdded) {
        viewModelScope.launch {
            runCatching {
                val uri = if (event.fromCamera) {
                    imageWatermarkHelper.applyLocationWatermark(event.uri)
                } else {
                    event.uri
                }
                withContext(coroutineDispatcher) { addDocumentUseCase(uri, event.type) }
            }.onFailure {
                Timber.e(it, "Failed to save document")
                onEffect(HomeEffect.ShowError(string.error_saving_document))
            }
        }
    }

    private fun removeDocument(id: String) {
        viewModelScope.launch {
            runCatching {
                withContext(coroutineDispatcher) { deleteDocumentUseCase(id) }
            }.onFailure {
                Timber.e(it, "Failed to remove document id=$id")
                onEffect(HomeEffect.ShowError(string.error_removing_document))
            }
        }
    }

    private fun onEffect(effect: HomeEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }
}
