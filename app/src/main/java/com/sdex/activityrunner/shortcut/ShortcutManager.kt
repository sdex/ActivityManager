package com.sdex.activityrunner.shortcut

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.sdex.activityrunner.R
import timber.log.Timber

fun createShortcut(
    context: Context,
    name: String,
    intent: Intent,
    icon: Bitmap?,
): Boolean {
    val iconCompat = try {
        if (icon != null) {
            IconCompat.createWithAdaptiveBitmap(icon)
        } else {
            null
        }
    } catch (e: Exception) {
        Timber.i(e)
        // android.os.TransactionTooLargeException
        // the icon is too big, fall back to the default icon
        null
    }
    return createShortcut(
        context = context,
        name = name,
        intent = intent,
        icon = iconCompat,
    )
}

private fun createShortcut(
    context: Context,
    name: String,
    intent: Intent,
    icon: IconCompat?,
): Boolean {
    val isRateLimitingActive = ShortcutManagerCompat.isRateLimitingActive(context)
    val shortcutIcon = icon ?: IconCompat.createWithResource(context, R.drawable.bookmark_24px)
    if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
        val shortcutId = name + System.currentTimeMillis()
        val pinShortcutInfo = ShortcutInfoCompat.Builder(context, shortcutId)
            .setIcon(shortcutIcon)
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
