package com.sdex.activityrunner.extensions

import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import com.sdex.activityrunner.R

fun RecyclerView.addDivider(@DrawableRes divider: Int = R.drawable.list_divider) {
  val dividerDrawable = ContextCompat.getDrawable(context, divider)
  val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
  dividerDrawable?.let { dividerItemDecoration.setDrawable(it) }
  addItemDecoration(dividerItemDecoration)
}