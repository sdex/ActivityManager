package com.sdex.activityrunner.extensions

import android.app.Activity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView

fun androidx.recyclerview.widget.RecyclerView.addDivider(activity: Activity) {
  val styledAttributes = activity.theme.obtainStyledAttributes(intArrayOf(android.R.attr.listDivider))
  val dividerDrawable = styledAttributes.getDrawable(0)
  val dividerItemDecoration = androidx.recyclerview.widget.DividerItemDecoration(context, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL)
  dividerDrawable?.let { dividerItemDecoration.setDrawable(it) }
  addItemDecoration(dividerItemDecoration)
}
