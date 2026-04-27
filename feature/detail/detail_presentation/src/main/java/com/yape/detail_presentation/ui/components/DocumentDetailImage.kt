package com.yape.detail_presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.yape.designsystem.theme.DocVaultTheme
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable

@Composable
fun DocumentDetailImage(
    bitmap: ImageBitmap,
    modifier: Modifier = Modifier
) {
    val state = rememberZoomableState()

    Image(
        bitmap = bitmap,
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .fillMaxSize()
            .zoomable(state)
    )
}

@Preview
@Composable
private fun DocumentDetailImagePreview() {
    DocVaultTheme {
        DocumentDetailImage(bitmap = previewBitmap())
    }
}

private fun previewBitmap(width: Int = 400, height: Int = 300): ImageBitmap =
    ImageBitmap(width, height).also { bmp ->
        Canvas(bmp).drawRect(0f, 0f, width.toFloat(), height.toFloat(), Paint().apply { color = Color.LightGray })
    }
