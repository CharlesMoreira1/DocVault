package com.yape.home_presentation.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yape.designsystem.theme.DocVaultTheme
import com.yape.document_domain.model.DocumentFilter
import com.yape.home_presentation.R

@Composable
fun HomeFilterBar(
    selected: DocumentFilter,
    onFilterSelected: (DocumentFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DocumentFilter.entries.forEach { filter ->
            FilterChip(
                selected = selected == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(stringResource(filter.labelRes)) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeFilterBarPreview(
    @PreviewParameter(DocumentFilterPreviewProvider::class) selected: DocumentFilter
) {
    DocVaultTheme {
        HomeFilterBar(selected = selected, onFilterSelected = {})
    }
}

@get:StringRes
private val DocumentFilter.labelRes: Int
    get() = when (this) {
        DocumentFilter.ALL -> R.string.filter_all
        DocumentFilter.IMAGE -> R.string.filter_images
        DocumentFilter.PDF -> R.string.filter_pdfs
    }

private class DocumentFilterPreviewProvider : PreviewParameterProvider<DocumentFilter> {
    override val values = DocumentFilter.entries.asSequence()
}
