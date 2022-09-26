package com.sdex.activityrunner.about

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Message
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sdex.activityrunner.R
import com.sdex.activityrunner.commons.BaseDialogFragment
import com.sdex.activityrunner.util.IntentUtils

class LicensesDialogFragment : BaseDialogFragment() {

    private var scrollPosition = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        scrollPosition = savedInstanceState?.getInt(ARG_POSITION, 0) ?: 0
        val webView = createWebView(requireContext(), scrollPosition) {
            scrollPosition = it
        }
        webView.loadUrl("file:///android_asset/licenses.html")
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.about_open_source)
            .setView(webView)
            .setPositiveButton(android.R.string.ok, null)
            .create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(ARG_POSITION, scrollPosition)
        super.onSaveInstanceState(outState)
    }

    private fun createWebView(
        context: Context,
        position: Int,
        onPositionChanged: (Int) -> Unit
    ): WebView {
        val webView = object : WebView(context) {
            override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
                super.onScrollChanged(l, t, oldl, oldt)
                onPositionChanged(t)
            }
        }
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                view?.scrollTo(0, position)
            }
        }
        webView.settings.setSupportMultipleWindows(true)
        webView.webChromeClient = object : WebChromeClient() {
            override fun onCreateWindow(
                view: WebView, isDialog: Boolean,
                isUserGesture: Boolean, resultMsg: Message
            ): Boolean {
                view.hitTestResult.extra?.let { IntentUtils.openBrowser(requireContext(), it) }
                return false
            }
        }
        return webView
    }

    companion object {
        const val TAG = "LicensesDialogFragment"
        private const val ARG_POSITION = "arg_position"
    }
}
