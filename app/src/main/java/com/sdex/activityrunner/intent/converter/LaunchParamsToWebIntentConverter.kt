package com.sdex.activityrunner.intent.converter

import android.content.Intent
import com.sdex.activityrunner.intent.LaunchParams

class LaunchParamsToWebIntentConverter(
    private val launchParams: LaunchParams
) : Converter<String> {

    override fun convert(): String {
        val converter = LaunchParamsToIntentConverter(launchParams)
        val intent = converter.convert()
        val uri = intent.toUri(Intent.URI_INTENT_SCHEME)
        return uri.toString()
    }
}
