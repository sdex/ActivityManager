package com.sdex.activityrunner.extensions

import android.content.res.Resources
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.view.ViewGroup
import com.sdex.activityrunner.R

fun Snackbar.config(margin: Int = 12, elevation: Float = 6f) {
  setActionTextColor(ContextCompat.getColor(context, R.color.yellow))
  val marginPx = (margin * Resources.getSystem().displayMetrics.density).toInt()
  val params = view.layoutParams as ViewGroup.MarginLayoutParams
  params.setMargins(marginPx, marginPx, marginPx, marginPx)
  view.layoutParams = params
  view.setBackgroundResource(R.drawable.background_snackbar)
  ViewCompat.setElevation(view, elevation)
}