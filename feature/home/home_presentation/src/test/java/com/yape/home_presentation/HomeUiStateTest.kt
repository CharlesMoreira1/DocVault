package com.yape.home_presentation

import com.yape.document_domain.model.DocumentDomain
import com.yape.document_domain.model.DocumentFilter
import com.yape.document_domain.model.DocumentType
import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeUiStateTest {

    private val imageDoc = DocumentDomain("img1", "photo.jpg", DocumentType.IMAGE, "path", 0L, 0L)
    private val pdfDoc = DocumentDomain("pdf1", "doc.pdf", DocumentType.PDF, "path", 0L, 0L)
    private val baseState = HomeUiState(documentDomains = persistentListOf(imageDoc, pdfDoc))

    @Test
    fun given_filter_all_when_filtered_then_returns_all_documents() {
        val state = baseState.copy(filter = DocumentFilter.ALL)

        assertEquals(2, state.filteredDocumentDomains.size)
    }

    @Test
    fun given_filter_image_when_filtered_then_returns_only_image_documents() {
        val state = baseState.copy(filter = DocumentFilter.IMAGE)

        val result = state.filteredDocumentDomains

        assertEquals(1, result.size)
        assertEquals(DocumentType.IMAGE, result[0].type)
    }

    @Test
    fun given_filter_pdf_when_filtered_then_returns_only_pdf_documents() {
        val state = baseState.copy(filter = DocumentFilter.PDF)

        val result = state.filteredDocumentDomains

        assertEquals(1, result.size)
        assertEquals(DocumentType.PDF, result[0].type)
    }
}
