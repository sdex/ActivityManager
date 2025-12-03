package com.sdex.activityrunner.about

import android.content.Context
import android.webkit.JavascriptInterface
import androidx.appcompat.view.ContextThemeWrapper
import com.sdex.activityrunner.R
import com.sdex.activityrunner.extensions.resolveColorAttr
import com.sdex.activityrunner.extensions.resolveIntAttr
import com.sdex.activityrunner.extensions.resolveResIdAttr
import com.sdex.activityrunner.extensions.toCssColor
import com.sdex.activityrunner.extensions.toDp
import com.google.android.material.R as MaterialR

class LicenseThemeJSInterface(val context: Context) {

    @JavascriptInterface
    fun getThemeStyle(): String {
        val htmlBackground = context.resolveColorAttr(MaterialR.attr.colorSurfaceContainerHigh).toCssColor()
        val htmlColor = context.resolveColorAttr(MaterialR.attr.colorOnSurfaceVariant).toCssColor()
        val htmlLetterSpacing =
            ContextThemeWrapper(context, context.resolveResIdAttr(MaterialR.attr.textAppearanceBodyMedium))
                .resolveIntAttr(android.R.attr.letterSpacing)
        val preBackground = context.resolveColorAttr(MaterialR.attr.colorSurfaceContainerHighest).toCssColor()
        val preColor = context.resolveColorAttr(MaterialR.attr.colorOnSurface).toCssColor()
        val preBorderRadius = context.resources.getDimensionPixelSize(R.dimen.default_card_corner_radius).toDp(context)
        val aColor = context.resolveColorAttr(MaterialR.attr.colorPrimaryVariant).toCssColor()
        return """
            html {
                background: $htmlBackground;
                color: $htmlColor;
                letter-spacing: $htmlLetterSpacing;
            }

            pre {
                background: $preBackground;
                color: $preColor;
                border-radius: ${preBorderRadius}px;
            }

            a {
                color: $aColor;
            }
        """.trimIndent()
    }
}
