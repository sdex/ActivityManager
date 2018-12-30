package com.sdex.activityrunner.extensions

import android.content.res.Resources
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import android.view.ViewGroup
import com.sdex.activityrunner.R

fun com.google.android.material.snackbar.Snackbar.config(margin: Int = 12, elevation: Float = 6f) {
  setActionTextColor(ContextCompat.getColor(context, R.color.yellow))
  val marginPx = (margin * Resources.getSystem().displayMetrics.density).toInt()
  val params = view.layoutParams as ViewGroup.MarginLayoutParams
  params.setMargins(marginPx, marginPx, marginPx, marginPx)
  view.layoutParams = params
  view.setBackgroundResource(R.drawable.background_snackbar)
  ViewCompat.setElevation(view, elevation)
}
