package com.sdex.activityrunner.extensions

import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.addDividerItemDecoration() {
    addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
}
