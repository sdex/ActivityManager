package com.sdex.activityrunner.extensions

import android.content.Intent
import android.os.Build
import android.os.Bundle
import java.io.Serializable

fun Intent.getFlagsList(): List<String> {
    val declaredFields = Intent::class.java.declaredFields
    val list = ArrayList<String>()
    for (field in declaredFields) {
        if (field.name.startsWith("FLAG_")) {
            try {
                val flag = field.getInt(null)
                if (flags and flag != 0) {
                    list.add(field.name)
                }
            } catch (_: IllegalArgumentException) {
            } catch (_: IllegalAccessException) {
            }
        }
    }
    return list
}

inline fun <reified T: Serializable?> Bundle.serializable(key: String): T? = when {
    isAndroidT() -> getSerializable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializable(key) as? T
}

inline fun <reified T: Serializable?> Intent.serializable(key: String): T? = when {
    isAndroidT() -> getSerializableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
}

inline fun <reified T> Intent.parcelable(key: String): T? = when {
    isAndroidT() -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

inline fun <reified T> Bundle.parcelable(key: String): T? = when {
    isAndroidT() -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}

fun isAndroidT() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
