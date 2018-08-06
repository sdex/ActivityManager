package com.sdex.activityrunner.shortcut

import android.content.Context
import android.content.Intent
import android.support.v4.content.pm.ShortcutInfoCompat
import android.support.v4.content.pm.ShortcutManagerCompat
import android.support.v4.graphics.drawable.IconCompat

object ShortcutManager {

  fun createShortcut(context: Context, name: String, intent: Intent, icon: IconCompat): Boolean {
    if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
      val pinShortcutInfo = ShortcutInfoCompat.Builder(context, name)
        .setIcon(icon)
        .setShortLabel(name)
        .setIntent(intent)
        .build()
      return ShortcutManagerCompat.requestPinShortcut(context, pinShortcutInfo, null)
    }
    return false
  }
}