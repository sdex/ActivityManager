package com.sdex.activityrunner.intent.converter

import android.content.Intent
import com.sdex.activityrunner.intent.LaunchParams
import com.sdex.activityrunner.intent.LaunchParamsExtra
import com.sdex.activityrunner.intent.LaunchParamsExtraType
import com.sdex.activityrunner.intent.getCategoriesValues
import com.sdex.activityrunner.intent.getFlagsValues
import com.sdex.activityrunner.intent.param.Category
import com.sdex.activityrunner.intent.param.Flag

class LaunchParamsToShellCommandConverter(
    private val launchParams: LaunchParams,
) : Converter<String> {

    override fun convert(): String {
        val args = mutableListOf("am", "start")
        // package name
        val packageName = if (launchParams.packageName.isNullOrEmpty()) {
            null
        } else {
            launchParams.packageName
        }
        // class name
        val className = if (launchParams.className.isNullOrEmpty()) {
            null
        } else {
            launchParams.className
        }
        if (packageName != null && className != null) {
            args += listOf("-n", "$packageName/$className")
        }
        // action
        val action = if (launchParams.action.isNullOrEmpty()) {
            Intent.ACTION_MAIN
        } else {
            launchParams.action
        }
        args += listOf("-a", action)
        // data and mime type
        if (!launchParams.data.isNullOrEmpty()) {
            args += listOf("-d", launchParams.data)
            if (!launchParams.mimeType.isNullOrEmpty()) {
                args += listOf("-t", launchParams.mimeType)
            }
        }
        // categories
        Category.list(launchParams.getCategoriesValues()).forEach { args += listOf("-c", it) }
        // flags
        val flags = Flag.list(launchParams.getFlagsValues()).fold(0) { acc, flag -> acc or flag }
        if (flags != 0) {
            args += listOf("-f", flags.toString())
        }
        // extras (boolean arrays are not supported by the am command)
        launchParams.extras.forEach { args += extraArgs(it) }
        // package-only intent: a trailing argument without ':' or '/' is treated as a package
        if (packageName != null && className == null) {
            args += packageName
        }
        return args.joinToString(" ") { escape(it) }
    }

    private fun extraArgs(extra: LaunchParamsExtra): List<String> {
        val option = if (extra.isArray) {
            when (extra.type) {
                LaunchParamsExtraType.INT -> "--eia"
                LaunchParamsExtraType.LONG -> "--ela"
                LaunchParamsExtraType.FLOAT -> "--efa"
                LaunchParamsExtraType.DOUBLE -> "--eda"
                LaunchParamsExtraType.BOOLEAN -> return emptyList()
                else -> "--esa"
            }
        } else {
            when (extra.type) {
                LaunchParamsExtraType.INT -> "--ei"
                LaunchParamsExtraType.LONG -> "--el"
                LaunchParamsExtraType.FLOAT -> "--ef"
                LaunchParamsExtraType.DOUBLE -> "--ed"
                LaunchParamsExtraType.BOOLEAN -> "--ez"
                else -> "--es"
            }
        }
        return listOf(option, extra.key, extra.value)
    }

    private fun escape(value: String): String {
        return if (SAFE_ARG_REGEX.matches(value)) {
            value
        } else {
            "'" + value.replace("'", "'\\''") + "'"
        }
    }

    companion object {

        private val SAFE_ARG_REGEX = Regex("[A-Za-z0-9._/:@+=,-]+")

        fun getUnsupportedExtras(launchParams: LaunchParams): List<LaunchParamsExtra> {
            return launchParams.extras.filter {
                it.isArray && it.type == LaunchParamsExtraType.BOOLEAN
            }
        }
    }
}
