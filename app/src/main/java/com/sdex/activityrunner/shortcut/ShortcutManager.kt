package com.sdex.activityrunner.shortcut

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.sdex.activityrunner.R
import timber.log.Timber

fun createShortcut(context: Context, name: String, intent: Intent, icon: Bitmap?): Boolean {
    val iconCompat = if (icon != null) {
        IconCompat.createWithBitmap(icon)
    } else {
        IconCompat.createWithResource(context, R.mipmap.ic_launcher)
    }
    return createShortcut(context, name, intent, iconCompat)
}

fun createShortcut(context: Context, name: String, intent: Intent, icon: IconCompat): Boolean {
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
        Timber.d("Pin shortcut: isRateLimitingActive=$isRateLimitingActive, " +
            "requestPinShortcutResult=$requestPinShortcutResult")
        return requestPinShortcutResult
    }
    Timber.d("Pin shortcut is not supported")
    return false
}
