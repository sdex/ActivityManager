package com.sdex.activityrunner.about

import android.content.Context
import android.webkit.JavascriptInterface
import com.google.android.material.R
import com.sdex.activityrunner.extensions.resolveColorAttr
import com.sdex.activityrunner.extensions.resolveDimenAttr
import com.sdex.activityrunner.extensions.toDp
import com.sdex.activityrunner.extensions.toHexColor

class LicenseThemeJSInterface(val context: Context) {
    @JavascriptInterface
    fun getHtmlBackground(): String {
        return context.resolveColorAttr(R.attr.colorSurfaceContainerHigh).toHexColor()
    }

    @JavascriptInterface
    fun getHtmlColor(): String {
        return context.resolveColorAttr(R.attr.colorOnSurfaceVariant).toHexColor()
    }

    @JavascriptInterface
    fun getHtmlLetterSpacing(): Float {
        return 0.01785714f // from TextAppearance.M3.Sys.Typescale.BodyMedium
    }

    @JavascriptInterface
    fun getPreBackground(): String {
        return context.resolveColorAttr(R.attr.colorSurfaceContainerHighest).toHexColor()
    }

    @JavascriptInterface
    fun getPreBorderRadius(): String {
        return context.resolveDimenAttr(R.attr.shapeCornerSizeMedium).toDp(context).toString() + "px"
    }

    @JavascriptInterface
    fun getPreColor(): String {
        return context.resolveColorAttr(R.attr.colorOnSurface).toHexColor()
    }

    @JavascriptInterface
    fun getAColor(): String {
        return context.resolveColorAttr(R.attr.colorPrimaryVariant).toHexColor()
    }
}
