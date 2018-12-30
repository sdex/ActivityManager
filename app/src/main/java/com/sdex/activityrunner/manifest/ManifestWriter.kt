package com.sdex.activityrunner.manifest

import android.content.Context
import androidx.annotation.WorkerThread
import java.io.File
import java.io.FileWriter

class ManifestWriter {

  @WorkerThread
  fun saveAndroidManifest(context: Context, packageName: String, manifest: String?): Boolean {
    if (manifest != null) {
      val pathResolver = ManifestPathResolver()
      val file = File(pathResolver.getPath(context, packageName))
      if (file.exists()) {
        file.delete()
      }
      if (file.createNewFile()) {
        val fileWriter = FileWriter(file)
        fileWriter.use { fileWriter.write(manifest) }
        return true
      }
    }
    return false
  }
}
