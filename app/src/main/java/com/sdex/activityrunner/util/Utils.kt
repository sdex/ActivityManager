package com.sdex.activityrunner.util

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.drawable.*
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES

object Utils {

  fun getBitmap(drawable: Drawable): Bitmap? {
    return if (drawable is BitmapDrawable) {
      drawable.bitmap
    } else {
      getBitmapFromDrawable(drawable)
    }
  }

  private fun getBitmapFromDrawable(drawable: Drawable): Bitmap? {
    if (VERSION.SDK_INT >= VERSION_CODES.O) {
      if (drawable is AdaptiveIconDrawable) {
        return getBitmapFromAdaptiveDrawable(drawable)
      }
    }
    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      if (drawable is VectorDrawable) {
        return getBitmapFromVectorDrawable(drawable)
      }
    }
    return null
  }

  @TargetApi(Build.VERSION_CODES.O)
  private fun getBitmapFromAdaptiveDrawable(drawable: AdaptiveIconDrawable): Bitmap {
    val drr = arrayOfNulls<Drawable>(2)
    drr[0] = drawable.background
    drr[1] = drawable.foreground

    val layerDrawable = LayerDrawable(drr)

    val width = layerDrawable.intrinsicWidth
    val height = layerDrawable.intrinsicHeight

    var bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    layerDrawable.setBounds(0, 0, canvas.width, canvas.height)
    layerDrawable.draw(canvas)

    bitmap = getBitmapClippedCircle(bitmap)

    return bitmap
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private fun getBitmapFromVectorDrawable(vectorDrawable: VectorDrawable): Bitmap {
    val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth,
      vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
    vectorDrawable.draw(canvas)
    return bitmap
  }

  private fun getBitmapClippedCircle(bitmap: Bitmap): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val path = Path()
    path.addCircle((width / 2).toFloat(), (height / 2).toFloat(),
      Math.min(width, height / 2).toFloat(), Path.Direction.CCW)
    val canvas = Canvas(outputBitmap)
    canvas.clipPath(path)
    canvas.drawBitmap(bitmap, 0f, 0f, null)
    return outputBitmap
  }

  fun isXLargeTablet(context: Context): Boolean {
    return context.resources.configuration.screenLayout and
      Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE
  }
}
