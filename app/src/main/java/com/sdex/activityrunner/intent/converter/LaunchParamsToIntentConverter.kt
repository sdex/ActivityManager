package com.sdex.activityrunner.intent.converter

import android.content.Intent
import androidx.core.net.toUri
import com.sdex.activityrunner.intent.LaunchParams
import com.sdex.activityrunner.intent.LaunchParamsExtra
import com.sdex.activityrunner.intent.LaunchParamsExtraType
import com.sdex.activityrunner.intent.getCategoriesValues
import com.sdex.activityrunner.intent.getFlagsValues
import com.sdex.activityrunner.intent.param.Category
import com.sdex.activityrunner.intent.param.Flag
import timber.log.Timber

class LaunchParamsToIntentConverter(
    private val launchParams: LaunchParams,
) : Converter<Intent> {

    override fun convert(): Intent {
        val intent = Intent()
        // package name
        val packageName = if (launchParams.packageName.isNullOrEmpty()) {
            null
        } else {
            launchParams.packageName
        }
        intent.`package` = packageName
        // class name
        val className = if (launchParams.className.isNullOrEmpty()) {
            null
        } else {
            launchParams.className
        }
        if (packageName != null && className != null) {
            intent.setClassName(packageName, className)
        }
        // action
        intent.action = if (launchParams.action.isNullOrEmpty()) {
            Intent.ACTION_MAIN
        } else {
            launchParams.action
        }
        // data and mime type
        if (!launchParams.data.isNullOrEmpty()) {
            val data = launchParams.data?.toUri()
            val type = if (launchParams.mimeType.isNullOrEmpty()) {
                null
            } else {
                launchParams.mimeType
            }
            intent.setDataAndType(data, type)
        }
        // categories
        Category.list(launchParams.getCategoriesValues()).forEach { intent.addCategory(it) }
        // flags
        Flag.list(launchParams.getFlagsValues()).forEach { intent.addFlags(it) }
        // extras
        launchParams.extras.forEach { putExtra(intent, it) }
        return intent
    }

    private fun putExtra(intent: Intent, extra: LaunchParamsExtra) {
        val key = extra.key
        val value = extra.value
        try {
            when (extra.type) {
                LaunchParamsExtraType.STRING -> intent.putExtra(key, value)
                LaunchParamsExtraType.INT -> intent.putExtra(key, value.toInt())
                LaunchParamsExtraType.LONG -> intent.putExtra(key, value.toLong())
                LaunchParamsExtraType.FLOAT -> intent.putExtra(key, value.toFloat())
                LaunchParamsExtraType.DOUBLE -> intent.putExtra(key, value.toDouble())
                LaunchParamsExtraType.BOOLEAN -> intent.putExtra(key, value.toBooleanStrict())
            }
        } catch (_: Exception) {
            Timber.d("Failed to parse the value")
        }
    }
}
