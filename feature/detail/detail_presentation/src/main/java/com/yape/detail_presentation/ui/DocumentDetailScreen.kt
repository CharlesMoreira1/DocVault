@file:OptIn(ExperimentalMaterial3Api::class)

package com.yape.detail_presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yape.detail_presentation.DocumentContent
import com.yape.detail_presentation.DocumentDetailViewModel
import com.yape.detail_presentation.R
import com.yape.detail_presentation.ui.components.DocumentDetailImage
import com.yape.detail_presentation.ui.components.DocumentDetailPdfViewer

@Composable
fun DocumentDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: DocumentDetailViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.documentDomain?.name ?: stringResource(R.string.document_default_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.nav_back_description)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when (val content = uiState.content) {
                is DocumentContent.Loading -> {
                    CircularProgressIndicator()
                }
                is DocumentContent.Image -> {
                    DocumentDetailImage(bitmap = content.bitmap)
                }
                is DocumentContent.Pdf -> {
                    DocumentDetailPdfViewer(pages = content.pages)
                }
                is DocumentContent.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            stringResource(R.string.error_opening_document),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            stringResource(content.messageRes),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
