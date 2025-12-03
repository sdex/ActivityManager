package com.sdex.activityrunner.extensions

import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red

fun Int.toCssColor() = "rgba(${red}, ${green}, ${blue}, ${alpha / 255f})"
