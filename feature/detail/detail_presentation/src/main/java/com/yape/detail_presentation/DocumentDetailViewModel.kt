package com.yape.detail_presentation

import androidx.lifecycle.ViewModel
import timber.log.Timber
import androidx.lifecycle.viewModelScope
import com.yape.common.R.string
import com.yape.common.helper.DocumentFileInputStreamHelper
import com.yape.document_domain.model.DocumentType
import com.yape.document_domain.usecase.GetDocumentsUseCase
import com.yape.document_domain.usecase.RecordAccessUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DocumentDetailViewModel(
    private val getDocumentsUseCase: GetDocumentsUseCase,
    private val recordAccessUseCase: RecordAccessUseCase,
    private val documentFileInputStreamHelper: DocumentFileInputStreamHelper,
    private val documentId: String,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(DocumentDetailUiState())
    val uiState: StateFlow<DocumentDetailUiState> = _uiState.asStateFlow()

    init {
        loadDocument()
    }

    private fun loadDocument() {
        viewModelScope.launch {
            val document = getDocumentsUseCase().first().find { it.id == documentId }

            if (document == null) {
                _uiState.update {
                    it.copy(content = DocumentContent.Error(string.error_document_not_found))
                }
                return@launch
            }

            _uiState.update { it.copy(documentDomain = document, content = DocumentContent.Loading) }
            launch(coroutineDispatcher) { recordAccessUseCase(documentId) }

            when (document.type) {
                DocumentType.IMAGE -> loadImage(document.relativePath)
                DocumentType.PDF -> loadPdf(document.relativePath)
            }
        }
    }

    private suspend fun loadImage(relativePath: String) {
        withContext(coroutineDispatcher) {
            runCatching {
                val bitmap = documentFileInputStreamHelper.getFileImage(relativePath)
                _uiState.update { it.copy(content = DocumentContent.Image(bitmap)) }
            }.onFailure {
                Timber.e(it, "Failed to load image: $relativePath")
                _uiState.update {
                    it.copy(content = DocumentContent.Error(string.error_opening_image))
                }
            }
        }
    }

    private suspend fun loadPdf(relativePath: String) {
        withContext(coroutineDispatcher) {
            runCatching {
                val pages = documentFileInputStreamHelper.getFilePDF(relativePath, documentId)
                _uiState.update { it.copy(content = DocumentContent.Pdf(pages.toImmutableList())) }
            }.onFailure {
                Timber.e(it, "Failed to load PDF: $relativePath")
                _uiState.update {
                    it.copy(content = DocumentContent.Error(string.error_opening_pdf))
                }
            }.also {
                documentFileInputStreamHelper.deleteFilePDF(documentId)
            }
        }
    }
}
