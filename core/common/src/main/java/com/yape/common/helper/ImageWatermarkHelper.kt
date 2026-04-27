package com.yape.common.helper

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import android.location.LocationManager.PASSIVE_PROVIDER
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.core.content.FileProvider
import com.yape.common.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume
import timber.log.Timber

class ImageWatermarkHelper(private val context: Context) {

    suspend fun applyLocationWatermark(uri: Uri): Uri = withContext(Dispatchers.IO) {
        val location = getLastLocation()
        val address = location?.let { getAddressText(it.latitude, it.longitude) }
            ?: context.getString(R.string.watermark_location_unavailable)
        val timestamp = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

        val original = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
            ?: return@withContext uri

        val watermarked = original.withWatermark("$address\n$timestamp")

        val outFile = File(
            File(context.cacheDir, "temp_camera").also { it.mkdirs() },
            "wm_${System.currentTimeMillis()}.jpg"
        )
        FileOutputStream(outFile).use { watermarked.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        FileProvider.getUriForFile(context, "${context.packageName}.provider", outFile)
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation(): Location? = runCatching {
        val manager = context.getSystemService(LOCATION_SERVICE) as LocationManager
        manager.getLastKnownLocation(GPS_PROVIDER)
            ?: manager.getLastKnownLocation(NETWORK_PROVIDER)
            ?: manager.getLastKnownLocation(PASSIVE_PROVIDER)
    }.onFailure { Timber.w(it, "Failed to retrieve last location") }
        .getOrNull()

    private suspend fun getAddressText(lat: Double, lon: Double): String {
        if (Geocoder.isPresent().not()) return formatCoords(lat, lon)
        val geocoder = Geocoder(context, Locale.getDefault())
        return if (SDK_INT >= TIRAMISU) {
            suspendCancellableCoroutine { cont ->
                geocoder.getFromLocation(lat, lon, 1) { addresses ->
                    cont.resume(addresses.firstOrNull()?.toText() ?: formatCoords(lat, lon))
                }
            }
        } else {
            @Suppress("DEPRECATION")
            runCatching {
                geocoder.getFromLocation(lat, lon, 1)?.firstOrNull()?.toText()
            }.onFailure { Timber.w(it, "Failed to geocode coordinates") }
                .getOrNull() ?: formatCoords(lat, lon)
        }
    }

    private fun Address.toText(): String = buildString {
        thoroughfare?.let { append(it) }
        subThoroughfare?.let { if (isNotEmpty()) append(", $it") }
        subLocality?.let { if (isNotEmpty()) append(" - $it") }
        locality?.let { if (isNotEmpty()) append(", $it") }
        if (isEmpty()) append(formatCoords(latitude, longitude))
    }

    private fun formatCoords(lat: Double, lon: Double) =
        "%.6f, %.6f".format(lat, lon)

    private fun Bitmap.withWatermark(text: String): Bitmap {
        val result = copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)
        val lines = text.split("\n")
        val textSize = (width * 0.038f).coerceAtLeast(30f)
        val padding = textSize * 0.7f
        val lineHeight = textSize * 1.5f
        val stripHeight = padding * 2 + lines.size * lineHeight

        canvas.drawRect(
            RectF(0f, height - stripHeight, width.toFloat(), height.toFloat()),
            Paint().apply { color = Color.BLACK; alpha = 150 }
        )

        val textPaint = Paint().apply {
            color = Color.WHITE
            this.textSize = textSize
            isAntiAlias = true
            setShadowLayer(3f, 1f, 1f, Color.BLACK)
        }
        lines.forEachIndexed { i, line ->
            canvas.drawText(
                line,
                padding,
                height - stripHeight + padding + (i + 1) * lineHeight,
                textPaint
            )
        }
        return result
    }
}