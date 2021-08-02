package com.sdex.activityrunner.manifest

import android.content.Context
import java.io.File

class ManifestPathResolver {

    fun getPath(context: Context, packageName: String): String {
        val file = File(context.cacheDir, "AndroidManifest($packageName).xml")
        return file.absolutePath
    }
}
