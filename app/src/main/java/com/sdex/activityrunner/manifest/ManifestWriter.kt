package com.sdex.activityrunner.manifest

import android.content.Context
import android.net.Uri
import androidx.annotation.WorkerThread
import java.io.FileWriter

interface ManifestWriter {

    @WorkerThread
    fun write(uri: Uri, data: String)
}

class DefaultManifestWriter(
    private val context: Context,
) : ManifestWriter {

    @WorkerThread
    override fun write(uri: Uri, data: String) {
        context.contentResolver.openFileDescriptor(uri, "w")?.use {
            val fileWriter = FileWriter(it.fileDescriptor)
            fileWriter.use { fileWriter.write(data) }
        }
    }
}
