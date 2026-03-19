package com.sdex.activityrunner.intent

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LaunchParams(
    val packageName: String? = null,
    val className: String? = null,
    val action: String? = null,
    val data: String? = null,
    val mimeType: String? = null,
    val categories: List<Int> = emptyList(),
    val flags: List<Int> = emptyList(),
    val extras: List<LaunchParamsExtra> = emptyList(),
) : Parcelable
