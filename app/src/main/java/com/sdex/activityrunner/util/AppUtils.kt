package com.sdex.activityrunner.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri

object AppUtils {

    const val REPOSITORY = "https://github.com/sdex/ActivityManager"
    const val ISSUES_TRACKER = "$REPOSITORY/issues"
    const val CHANGELOG = "$REPOSITORY/blob/main/CHANGELOG.md"
    const val SUGGESTION_LINK = "$REPOSITORY/discussions/categories/ideas"
    const val TRANSLATE_LINK = "https://crowdin.com/project/activity-manager"

    fun openLink(context: Context, url: String) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
        } catch (_: Exception) {
            Toast.makeText(
                context,
                "Failed to open link",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    fun openAppMarket(context: Context, appPackageName: String?) {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    ("market://details?id=$appPackageName").toUri(),
                ),
            )
        } catch (_: ActivityNotFoundException) {
            try {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        "http://play.google.com/store/apps/details?id=$appPackageName".toUri(),
                    ),
                )
            } catch (_: Exception) {
                Toast.makeText(
                    context,
                    "Failed to open Play Store",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }
}
