package com.sdex.activityrunner.extensions

import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.addDividerItemDecoration() {
    addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
}

fun RecyclerView.fitsSystemWindowsInsets(
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false
) {
    this.clipToPadding = false
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, windowInsets ->
        val systemBarsInsets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        val displayCutoutInsets = windowInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
        val insets = Insets.of(
            maxOf(systemBarsInsets.left, displayCutoutInsets.left),
            maxOf(systemBarsInsets.top, displayCutoutInsets.top),
            maxOf(systemBarsInsets.right, displayCutoutInsets.right),
            maxOf(systemBarsInsets.bottom, displayCutoutInsets.bottom)
        )
        v.setPadding(
            if (left) insets.left else 0,
            if (top) insets.top else 0,
            if (right) insets.right else 0,
            if (bottom) insets.bottom else 0,
        )
        WindowInsetsCompat.CONSUMED
    }
}
