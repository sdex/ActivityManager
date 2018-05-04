package com.sdex.activityrunner.util

import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import com.sdex.activityrunner.R

fun RecyclerView.addDivider() {
  val context = this.context
  val dividerDrawable = ContextCompat.getDrawable(context, R.drawable.list_divider)
  if (dividerDrawable != null) {
    val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
    dividerItemDecoration.setDrawable(dividerDrawable)
    this.addItemDecoration(dividerItemDecoration)
  }
}

