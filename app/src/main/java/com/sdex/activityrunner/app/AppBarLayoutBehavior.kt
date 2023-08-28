package com.sdex.activityrunner.app

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout

/**
 * Fix no change in app bar color after scrolling view slide to top on Android 12+ devices.
 * @author Jesse205
 * */
class AppBarLayoutBehavior(context: Context?, attrs: AttributeSet?) :
    AppBarLayout.Behavior(context, attrs) {
    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: AppBarLayout,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        super.onNestedScroll(
            coordinatorLayout,
            child,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type,
            consumed
        )
        // Fix no change in app bar color after scrolling view slide to top on Android 12+ devices.
        if (child.isLiftOnScroll) {
            child.isLifted = dyUnconsumed >= 0
        }
    }

}
