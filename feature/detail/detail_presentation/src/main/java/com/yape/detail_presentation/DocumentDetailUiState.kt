package com.yape.detail_presentation

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.ImageBitmap
import com.yape.document_domain.model.DocumentDomain
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class DocumentDetailUiState(
    val documentDomain: DocumentDomain? = null,
    val content: DocumentContent = DocumentContent.Loading,
    val watermarkText: String = "",
    val showAccessLog: Boolean = false
)

@Stable
sealed class DocumentContent {
    object Loading : DocumentContent()
    data class Image(val bitmap: ImageBitmap) : DocumentContent()
    data class Pdf(val pages: ImmutableList<ImageBitmap>) : DocumentContent()
    data class Error(@param:StringRes val messageRes: Int) : DocumentContent()
}
