package com.yape.home_presentation

import android.net.Uri
import com.yape.document_domain.model.DocumentFilter
import com.yape.document_domain.model.DocumentType

sealed class HomeEvent {
    data class FilterChanged(val filter: DocumentFilter) : HomeEvent()
    object FabClicked : HomeEvent()
    object AddFromGallery : HomeEvent()
    object AddFromCamera : HomeEvent()
    object DismissAddOptions : HomeEvent()
    data class DocumentAdded(val uri: Uri, val type: DocumentType, val fromCamera: Boolean = false) : HomeEvent()
    data class DocumentClicked(val id: String) : HomeEvent()
    data class DocumentRemoveRequested(val id: String) : HomeEvent()
}
