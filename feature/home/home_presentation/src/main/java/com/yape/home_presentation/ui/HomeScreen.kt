@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)

package com.yape.home_presentation.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.CAMERA
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.yape.common.R.string
import com.yape.common.helper.showBiometricPrompt
import com.yape.designsystem.theme.DocVaultTheme
import com.yape.document_domain.model.DocumentDomain
import com.yape.document_domain.model.DocumentType
import com.yape.home_presentation.HomeEffect
import com.yape.home_presentation.HomeEvent
import com.yape.home_presentation.HomeUiState
import com.yape.home_presentation.HomeViewModel
import com.yape.home_presentation.R
import com.yape.home_presentation.ui.components.HomeAddFab
import com.yape.home_presentation.ui.components.HomeCard
import com.yape.home_presentation.ui.components.HomeFilterBar
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun HomeScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: HomeViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val launchGallery = rememberGalleryLauncher(context) { uri, type ->
        viewModel.handleEvent(HomeEvent.DocumentAdded(uri, type))
    }

    val launchCamera = rememberCameraLauncher(
        context = context,
        snackbarHostState = snackbarHostState,
        onImageCaptured = { uri ->
            viewModel.handleEvent(HomeEvent.DocumentAdded(uri, DocumentType.IMAGE, fromCamera = true))
        }
    )

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is HomeEffect.LaunchGallery -> {
                    launchGallery()
                }
                is HomeEffect.LaunchCamera -> {
                    launchCamera()
                }
                is HomeEffect.NavigateToDetail -> {
                    showBiometricPrompt(
                        context = context,
                        onSuccess = { onNavigateToDetail(effect.id) },
                        onError = { message ->
                            scope.launch { snackbarHostState.showSnackbar(message) }
                        }
                    )
                }
                is HomeEffect.ShowError -> {
                    @SuppressLint("LocalContextGetResourceValueCall")
                    scope.launch { snackbarHostState.showSnackbar(context.getString(effect.messageRes)) }
                }
            }
        }
    }

    HomeContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        handleEvent = viewModel::handleEvent,
        onDeleteRequested = { id ->
            showBiometricPrompt(
                context = context,
                onSuccess = { viewModel.handleEvent(HomeEvent.DocumentRemoveRequested(id)) },
                onError = { message -> scope.launch { snackbarHostState.showSnackbar(message) } }
            )
        }
    )
}

@Composable
private fun rememberGalleryLauncher(
    context: Context,
    onDocumentPicked: (Uri, DocumentType) -> Unit
): () -> Unit {
    val launcher = rememberLauncherForActivityResult(OpenDocument()) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        val mimeType = context.contentResolver.getType(uri) ?: ""
        val type = if (mimeType.startsWith("image/")) DocumentType.IMAGE else DocumentType.PDF
        onDocumentPicked(uri, type)
    }
    return { launcher.launch(arrayOf("image/*", "application/pdf")) }
}

@Composable
private fun rememberCameraLauncher(
    context: Context,
    snackbarHostState: SnackbarHostState,
    onImageCaptured: (Uri) -> Unit
): () -> Unit {
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    val permissionsState = rememberMultiplePermissionsState(listOf(CAMERA, ACCESS_COARSE_LOCATION))
    val cameraPermission = permissionsState.permissions.first { it.permission == CAMERA }

    val launcher = rememberLauncherForActivityResult(TakePicture()) { success ->
        val uri = photoUri
        photoUri = null
        if (success && uri != null) onImageCaptured(uri)
    }

    LaunchedEffect(cameraPermission.status) {
        val uri = photoUri ?: return@LaunchedEffect
        when {
            cameraPermission.status.isGranted -> launcher.launch(uri)
            cameraPermission.status.shouldShowRationale -> {
                photoUri = null
                snackbarHostState.showSnackbar(context.getString(R.string.permission_camera_denied))
            }
            else -> {
                photoUri = null
                val result = snackbarHostState.showSnackbar(
                    message = context.getString(R.string.permission_camera_permanently_denied),
                    actionLabel = context.getString(R.string.action_open),
                    duration = SnackbarDuration.Long
                )
                if (result == SnackbarResult.ActionPerformed) {
                    context.startActivity(
                        Intent(ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                    )
                }
            }
        }
    }

    return {
        val uri = createTempPhotoUri(context)
        photoUri = uri
        if (cameraPermission.status.isGranted) launcher.launch(uri)
        else permissionsState.launchMultiplePermissionRequest()
    }
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    snackbarHostState: SnackbarHostState,
    handleEvent: (HomeEvent) -> Unit,
    onDeleteRequested: (String) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(string.app_name)) }) },
        floatingActionButton = {
            HomeAddFab(
                expanded = uiState.isFabExpanded,
                onFabClick = { handleEvent(HomeEvent.FabClicked) },
                onGalleryClick = { handleEvent(HomeEvent.AddFromGallery) },
                onCameraClick = { handleEvent(HomeEvent.AddFromCamera) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            HomeFilterBar(
                selected = uiState.filter,
                onFilterSelected = { handleEvent(HomeEvent.FilterChanged(it)) },
                modifier = Modifier.padding(vertical = 8.dp)
            )

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.filteredDocumentDomains.isEmpty() -> {
                    HomeEmptyState(modifier = Modifier.fillMaxSize())
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.filteredDocumentDomains, key = { it.id }) { document ->
                            HomeCard(
                                documentDomain = document,
                                onClick = { handleEvent(HomeEvent.DocumentClicked(document.id)) },
                                onDelete = { onDeleteRequested(document.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeEmptyState(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = stringResource(R.string.home_empty_title), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.home_empty_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview(
    @PreviewParameter(HomeUiStatePreviewProvider::class) uiState: HomeUiState
) {
    DocVaultTheme {
        HomeContent(
            uiState = uiState,
            snackbarHostState = remember { SnackbarHostState() },
            handleEvent = {},
            onDeleteRequested = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeEmptyStatePreview() {
    DocVaultTheme {
        HomeEmptyState(modifier = Modifier.fillMaxSize())
    }
}

private fun createTempPhotoUri(context: Context): Uri {
    val dir = File(context.cacheDir, "temp_camera").also { it.mkdirs() }
    val file = File(dir, "photo_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}

private class HomeUiStatePreviewProvider : PreviewParameterProvider<HomeUiState> {
    override val values = sequenceOf(
        HomeUiState(isLoading = true),
        HomeUiState(documentDomains = persistentListOf()),
        HomeUiState(
            documentDomains = persistentListOf(
                DocumentDomain(
                    "1", "Fake1.pdf",
                    DocumentType.PDF, "", 1_700_000_000_000L, 1_200_000L
                ),
                DocumentDomain(
                    "2", "Fake2.jpg",
                    DocumentType.IMAGE, "", 1_700_000_000_000L, 320_000L
                )
            )
        )
    )
}
