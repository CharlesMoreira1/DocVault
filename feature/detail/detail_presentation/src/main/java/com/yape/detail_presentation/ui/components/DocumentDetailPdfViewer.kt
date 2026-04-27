package com.yape.detail_presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yape.designsystem.theme.DocVaultTheme
import com.yape.detail_presentation.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable

@Composable
fun DocumentDetailPdfViewer(
    pages: ImmutableList<ImageBitmap>,
    modifier: Modifier = Modifier
) {
    val state = rememberZoomableState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .zoomable(state)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(pages) { index, page ->
                Image(
                    bitmap = page,
                    contentDescription = stringResource(R.string.pdf_page_description, index + 1),
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview
@Composable
private fun DocumentDetailPdfViewerPreview() {
    DocVaultTheme {
        DocumentDetailPdfViewer(pages = List(3) { previewBitmap() }.toImmutableList())
    }
}

private fun previewBitmap(width: Int = 400, height: Int = 565): ImageBitmap =
    ImageBitmap(width, height).also { bmp ->
        Canvas(bmp).drawRect(0f, 0f, width.toFloat(), height.toFloat(), Paint().apply { color = Color.LightGray })
    }
