package com.sdex.activityrunner.extensions

import android.view.View
import android.view.ViewTreeObserver

@Suppress("DEPRECATION")
fun View.doAfterMeasure(callback: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(
        object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                callback()
            }
        }
    )
}