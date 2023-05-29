package com.sdex.activityrunner.manifest

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import com.sdex.activityrunner.R
import java.io.File

object ShareProvider {

    fun share(activity: Activity, packageName: String) {
        val pathResolver = ManifestPathResolver()
        val path = pathResolver.getPath(activity, packageName)
        val file = File(path)
        val uri = FileProvider.getUriForFile(activity, activity.packageName + ".fileprovider", file)
        val intent = ShareCompat.IntentBuilder(activity)
            .setType("text/xml")
            .setStream(uri)
            .setChooserTitle(R.string.dialog_share_title)
            .createChooserIntent()
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            activity.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(activity, "Failed to share file", Toast.LENGTH_SHORT).show()
        }
    }
}
