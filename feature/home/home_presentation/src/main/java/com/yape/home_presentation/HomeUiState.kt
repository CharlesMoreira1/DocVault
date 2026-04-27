package com.yape.home_presentation

import androidx.compose.runtime.Immutable
import com.yape.document_domain.model.DocumentDomain
import com.yape.document_domain.model.DocumentFilter
import com.yape.document_domain.model.DocumentType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Immutable
data class HomeUiState(
    val documentDomains: ImmutableList<DocumentDomain> = persistentListOf(),
    val filter: DocumentFilter = DocumentFilter.ALL,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFabExpanded: Boolean = false
) {
    val filteredDocumentDomains: ImmutableList<DocumentDomain>
        get() = when (filter) {
            DocumentFilter.ALL -> documentDomains
            DocumentFilter.IMAGE -> documentDomains.filter { it.type == DocumentType.IMAGE }.toImmutableList()
            DocumentFilter.PDF -> documentDomains.filter { it.type == DocumentType.PDF }.toImmutableList()
        }
}
