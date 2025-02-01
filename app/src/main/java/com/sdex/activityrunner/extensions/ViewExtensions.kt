package com.sdex.activityrunner.extensions

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

@Suppress("DEPRECATION")
fun View.doAfterMeasure(callback: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(
        object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                callback()
            }
        }
    )
}

@ColorInt
fun Context.resolveColorAttr(@AttrRes colorAttr: Int): Int {
    val resolvedAttr = resolveThemeAttr(colorAttr)
    // resourceId is used if it's a ColorStateList, and data if it's a color reference or a hex color
    val colorRes = if (resolvedAttr.resourceId != 0) resolvedAttr.resourceId else resolvedAttr.data
    return ContextCompat.getColor(this, colorRes)
}

fun Context.resolveThemeAttr(@AttrRes attrRes: Int): TypedValue {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrRes, typedValue, true)
    return typedValue
}

fun RecyclerView.addDividerItemDecoration() {
    addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
}

fun BottomSheetDialogFragment.createBottomSheetDialog(): BottomSheetDialog {
    val dialog = BottomSheetDialog(requireContext(), theme)
    // open bottom sheet with the expanded state in the landscape
    // https://stackoverflow.com/a/61813321/2894324
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    // avoid collapsed state in the landscape
    // https://stackoverflow.com/a/70244532/2894324
    dialog.behavior.skipCollapsed = true
    return dialog
}
