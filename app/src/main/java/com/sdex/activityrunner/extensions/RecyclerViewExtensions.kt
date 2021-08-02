package com.sdex.activityrunner.extensions

import android.app.Activity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.addDivider(activity: Activity) {
    val styledAttributes =
        activity.theme.obtainStyledAttributes(intArrayOf(android.R.attr.listDivider))
    val dividerDrawable = styledAttributes.getDrawable(0)
    val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
    dividerDrawable?.let { dividerItemDecoration.setDrawable(it) }
    addItemDecoration(dividerItemDecoration)
}
