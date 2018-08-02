package com.sdex.activityrunner.extensions

import android.os.Build
import android.view.View
import android.view.ViewTreeObserver

@Suppress("DEPRECATION")
fun View.doAfterMeasure(callback: () -> Unit) {
  viewTreeObserver.addOnGlobalLayoutListener(
    object : ViewTreeObserver.OnGlobalLayoutListener {
      override fun onGlobalLayout() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
          viewTreeObserver.removeOnGlobalLayoutListener(this)
        } else {
          viewTreeObserver.removeGlobalOnLayoutListener(this)
        }
        callback()
      }
    }
  )
}