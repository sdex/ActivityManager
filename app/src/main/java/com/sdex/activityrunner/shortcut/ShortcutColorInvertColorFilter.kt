package com.sdex.activityrunner.shortcut

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter

class ShortcutColorInvertColorFilter : ColorMatrixColorFilter(
    ColorMatrix(
        floatArrayOf(
            -1f, 0f, 0f, 0f, 255f,  // Red
            0f, -1f, 0f, 0f, 255f,  // Green
            0f, 0f, -1f, 0f, 255f,  // Blue
            0f, 0f, 0f, 1f, 0f,   // Alpha
        ),
    ),
)
