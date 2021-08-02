package com.sdex.commons.license

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import com.sdex.activityrunner.R
import com.sdex.commons.BaseDialogFragment

class LicensesDialogFragment : BaseDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val webView = createWebView(requireContext())
        webView.loadUrl("file:///android_asset/licenses.html")
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_license_title)
            .setView(webView)
            .setPositiveButton(R.string.dialog_license_close, null)
            .create()
    }

    private fun createWebView(context: Context): WebView {
        val webView = WebView(context)
        webView.settings.setSupportMultipleWindows(true)
        webView.webChromeClient = object : WebChromeClient() {
            override fun onCreateWindow(
                view: WebView, isDialog: Boolean,
                isUserGesture: Boolean, resultMsg: Message
            ): Boolean {
                val result = view.hitTestResult
                val data = result.extra
                if (data != null) {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(data))
                    context.startActivity(browserIntent)
                }
                return false
            }
        }
        return webView
    }

    companion object {

        const val TAG = "LicensesDialogFragment"
    }
}
