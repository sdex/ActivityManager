package com.sdex.activityrunner.intent.converter

import android.content.Intent
import com.sdex.activityrunner.intent.LaunchParams
import com.sdex.activityrunner.intent.LaunchParamsExtra

class LaunchParamsToWebIntentConverter(
    private val launchParams: LaunchParams,
) : Converter<String> {

    override fun convert(): String {
        val converter = LaunchParamsToIntentConverter(launchParams)
        val intent = converter.convert()
        val uri = intent.toUri(Intent.URI_INTENT_SCHEME)
        return uri.toString()
    }

    companion object {

        // Intent.toUri() serializes only scalar extras
        fun getUnsupportedExtras(launchParams: LaunchParams): List<LaunchParamsExtra> {
            return launchParams.extras.filter { it.isArray }
        }
    }
}
