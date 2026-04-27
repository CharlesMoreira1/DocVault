@file:OptIn(ExperimentalMaterial3Api::class)

package com.yape.home_presentation.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.yape.common.R.string
import com.yape.designsystem.theme.DocVaultTheme
import com.yape.document_domain.model.DocumentDomain
import com.yape.document_domain.model.DocumentType
import com.yape.home_presentation.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeCard(
    documentDomain: DocumentDomain,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DocumentTypeIcon(type = documentDomain.type)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = documentDomain.name,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${stringResource(documentDomain.type.labelRes)} • ${documentDomain.sizeBytes.toReadableSize()} • ${documentDomain.createdAt.toFormattedDate()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.card_delete_description),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun DocumentTypeIcon(type: DocumentType) {
    val (icon, containerColor, contentColor) = when (type) {
        DocumentType.IMAGE -> Triple(
            Icons.Default.Image,
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
        DocumentType.PDF -> Triple(
            Icons.Default.PictureAsPdf,
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer
        )
    }
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = icon,
                contentDescription = stringResource(type.labelRes),
                tint = contentColor,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeCardPreview(
    @PreviewParameter(DocumentDomainPreviewProvider::class) document: DocumentDomain
) {
    DocVaultTheme {
        HomeCard(documentDomain = document, onClick = {}, onDelete = {})
    }
}

@get:StringRes
private val DocumentType.labelRes: Int
    get() = when (this) {
        DocumentType.IMAGE -> string.document_type_image
        DocumentType.PDF -> string.document_type_pdf
    }

private fun Long.toReadableSize(): String {
    val kb = this / 1024.0
    val mb = kb / 1024.0
    return if (mb >= 1) "%.1f MB".format(mb) else "%.0f KB".format(kb)
}

private fun Long.toFormattedDate(): String =
    SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault()).format(Date(this))

private class DocumentDomainPreviewProvider : PreviewParameterProvider<DocumentDomain> {
    override val values = sequenceOf(
        DocumentDomain(
            id = "1", name = "Fake.pdf",
            type = DocumentType.PDF, relativePath = "",
            createdAt = 1_700_000_000_000L, sizeBytes = 2_500_000L
        ),
        DocumentDomain(
            id = "2", name = "Fake.jpg",
            type = DocumentType.IMAGE, relativePath = "",
            createdAt = 1_700_000_000_000L, sizeBytes = 450_000L
        )
    )
}
