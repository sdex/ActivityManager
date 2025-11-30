package com.sdex.activityrunner.extensions

fun Int.toHexColor() = "#%06X".format(0xFFFFFF and this)
