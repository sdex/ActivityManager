package com.sdex.activityrunner.shortcut

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.graphics.createBitmap
import kotlin.math.max

fun Bitmap.applyShortcutTweaks(
    isAdaptiveBitmap: Boolean,
    invertIconColors: Boolean,
): Bitmap {
    val paint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
        if (invertIconColors) {
            colorFilter = ShortcutColorInvertColorFilter()
        }
    }

    // To keep the source bitmap at 1:1 scale in the 72dp safe zone:
    // The total canvas must be 1.5x the largest dimension of the source.
    val sourceMaxSide = max(width, height)
    val canvasSize = (sourceMaxSide * 1.5f).toInt()

    val left = (canvasSize - width) / 2f
    val top = (canvasSize - height) / 2f

    val tweakedIcon = createBitmap(
        width = canvasSize,
        height = canvasSize,
        config = this.config ?: Bitmap.Config.ARGB_8888,
    )

    val canvas = Canvas(tweakedIcon)

    if (!isAdaptiveBitmap) {
        val backgroundColor = if (invertIconColors) {
            Color.BLACK
        } else {
            Color.WHITE
        }
        canvas.drawColor(backgroundColor)
    }

    canvas.drawBitmap(
        this,
        left,
        top,
        paint,
    )

    return tweakedIcon
}
