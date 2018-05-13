package com.sdex.activityrunner.intent

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LaunchParams(var packageName: String? = null,
                        var className: String? = null,
                        var action: String? = null,
                        var data: String? = null,
                        var mimeType: String? = null,
                        var categories: ArrayList<Int> = ArrayList(0),
                        var flags: ArrayList<Int> = ArrayList(0),
                        var extras: ArrayList<LaunchParamsExtra> = ArrayList(0)) : Parcelable