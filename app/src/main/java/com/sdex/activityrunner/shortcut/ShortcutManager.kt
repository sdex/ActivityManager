package com.sdex.activityrunner.shortcut

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.IconCompat
import com.sdex.activityrunner.R
import timber.log.Timber

fun createShortcut(
    context: Context,
    name: String,
    intent: Intent,
    icon: Bitmap?,
    invertIconColors: Boolean = false,
): Boolean {
    val iconCompat = try {
        if (icon != null) {
            IconCompat.createWithAdaptiveBitmap(icon.applyShortcutTweaks(invertIconColors))
        } else {
            IconCompat.createWithResource(context, R.mipmap.ic_launcher)
        }
    } catch (e: Exception) { // android.os.TransactionTooLargeException
        Timber.i(e)
        // the icon is too big, fall back to the default icon
        IconCompat.createWithResource(context, R.mipmap.ic_launcher)
    }

    return createShortcut(context, name, intent, iconCompat)
}

private fun createShortcut(
    context: Context,
    name: String,
    intent: Intent,
    icon: IconCompat,
): Boolean {
    val isRateLimitingActive = ShortcutManagerCompat.isRateLimitingActive(context)
    if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
        val pinShortcutInfo = ShortcutInfoCompat.Builder(context, name)
            .setIcon(icon)
            .setShortLabel(name)
            .setIntent(intent)
            .build()
        val requestPinShortcutResult = ShortcutManagerCompat.requestPinShortcut(
            context,
            pinShortcutInfo,
            null,
        )
        Timber.d(
            "Pin shortcut: isRateLimitingActive=$isRateLimitingActive, " +
                "requestPinShortcutResult=$requestPinShortcutResult",
        )
        return requestPinShortcutResult
    }
    Timber.d("Pin shortcut is not supported")
    return false
}

private fun Bitmap.applyShortcutTweaks(
    invertIconColors: Boolean = false,
): Bitmap {
    val paint = Paint().apply {
        if (invertIconColors) {
            colorFilter = ShortcutColorInvertColorFilter()
        }
    }

    val paddedWidth = width + 128
    val paddedHeight = height + 128
    val tweakedIcon = createBitmap(
        width = paddedWidth,
        height = paddedHeight,
        config = config ?: Bitmap.Config.ARGB_8888,
    )
    val canvas = Canvas(tweakedIcon)
    canvas.drawBitmap(
        this,
        (paddedWidth - width) / 2f,
        (paddedHeight - height) / 2f,
        paint,
    )

    return tweakedIcon
}
