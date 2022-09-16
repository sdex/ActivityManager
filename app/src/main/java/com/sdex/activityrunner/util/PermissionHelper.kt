@file:JvmName("PermissionHelper")

package com.sdex.commons.content

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

fun getStoragePermission(): String = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> READ_MEDIA_IMAGES
    Build.VERSION.SDK_INT > Build.VERSION_CODES.Q -> READ_EXTERNAL_STORAGE
    else -> WRITE_EXTERNAL_STORAGE
}

fun isStoragePermissionGranted(context: Context): Boolean {
    return hasPermission(context, getStoragePermission())
}

private fun hasPermission(context: Context, permission: String): Boolean =
    ContextCompat.checkSelfPermission(context, permission) ==
        PackageManager.PERMISSION_GRANTED
