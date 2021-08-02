package com.sdex.activityrunner.intent.converter

import android.content.Intent
import android.net.Uri
import android.util.Log
import com.sdex.activityrunner.intent.*
import com.sdex.activityrunner.intent.param.Category
import com.sdex.activityrunner.intent.param.Flag
import java.util.*

class LaunchParamsToIntentConverter(private val launchParams: LaunchParams) : Converter<Intent> {

    override fun convert(): Intent {
        val intent = Intent()
        // package name
        val packageName =
            if (launchParams.packageName.isNullOrEmpty()) null
            else launchParams.packageName
        intent.`package` = packageName
        // class name
        val className =
            if (launchParams.className.isNullOrEmpty()) null
            else launchParams.className
        if (packageName != null && className != null) {
            intent.setClassName(packageName, className)
        }
        // action
        intent.action =
            if (launchParams.action.isNullOrEmpty()) Intent.ACTION_MAIN
            else launchParams.action
        // data and mime type
        val data = Uri.parse(launchParams.data)
        val type =
            if (launchParams.mimeType.isNullOrEmpty()) null
            else launchParams.mimeType
        intent.setDataAndType(data, type)
        // categories
        val categories = Category.list(launchParams.getCategoriesValues())
        for (category in categories) {
            intent.addCategory(category)
        }
        // flags
        val flags = Flag.list(launchParams.getFlagsValues())
        for (flag in flags) {
            intent.addFlags(flag)
        }
        // extras
        val extras = launchParams.extras
        addExtras(intent, extras)
        return intent
    }

    private fun addExtras(intent: Intent, extras: ArrayList<LaunchParamsExtra>) {
        for (extra in extras) {
            val type = extra.type
            val key = extra.key
            val value = extra.value
            try {
                when (type) {
                    LaunchParamsExtraType.STRING -> intent.putExtra(key, value)
                    LaunchParamsExtraType.INT -> intent.putExtra(key, Integer.parseInt(value))
                    LaunchParamsExtraType.LONG -> intent.putExtra(
                        key,
                        java.lang.Long.parseLong(value)
                    )
                    LaunchParamsExtraType.FLOAT -> intent.putExtra(
                        key,
                        java.lang.Float.parseFloat(value)
                    )
                    LaunchParamsExtraType.DOUBLE -> intent.putExtra(
                        key,
                        java.lang.Double.parseDouble(value)
                    )
                    LaunchParamsExtraType.BOOLEAN -> intent.putExtra(
                        key,
                        java.lang.Boolean.parseBoolean(value)
                    )
                }
            } catch (e: NumberFormatException) {
                Log.d(TAG, "Failed to parse number")
            }
        }
    }

    companion object {

        private const val TAG = "LaunchParamsIntent"
    }
}
