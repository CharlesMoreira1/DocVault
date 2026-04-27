package com.yape.common.helper

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap
import java.io.File
import kotlin.io.use
import kotlin.use

class DocumentFileInputStreamHelper(
    private val context: Context,
    private val secureFileFacade: SecureFileFacade,
) {
    private val tempFile: (String) -> File = { documentId ->
        File(context.cacheDir, "tmp_$documentId.pdf")
    }

    fun getFileImage(relativePath: String): ImageBitmap {
        val bytes = secureFileFacade
            .openDecryptedInputStream(relativePath)
            .use { it.readBytes() }

        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            ?: error("Failed to decode image")

        return bitmap.asImageBitmap()
    }

    fun getFilePDF(relativePath: String, documentId: String): List<ImageBitmap> {
        secureFileFacade.openDecryptedInputStream(relativePath).use { input ->
            tempFile(documentId).outputStream().use { input.copyTo(it) }
        }

        val fd = ParcelFileDescriptor.open(tempFile(documentId), ParcelFileDescriptor.MODE_READ_ONLY)
        val renderer = PdfRenderer(fd)

        val pages = (0 until renderer.pageCount).map { index ->
            renderer.openPage(index).use { page ->
                createBitmap(page.width * 2, page.height * 2).also { bmp ->
                    bmp.eraseColor(Color.WHITE)
                    page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                }.asImageBitmap()
            }
        }

        renderer.close()
        fd.close()

        return pages
    }

    fun deleteFilePDF(documentId: String) {
        tempFile.invoke(documentId)
    }
}