package com.sdex.activityrunner.intent.converter

import com.sdex.activityrunner.db.history.HistoryModel
import com.sdex.activityrunner.intent.LaunchParams

class LaunchParamsToHistoryConverter(
    private val launchParams: LaunchParams
) : Converter<HistoryModel> {

    override fun convert(): HistoryModel {
        val integerListSerializer = IntegerListSerializer()
        val extrasSerializer = ExtrasSerializer()
        return HistoryModel().apply {
            timestamp = System.currentTimeMillis()
            packageName = launchParams.packageName
            className = launchParams.className
            action = launchParams.action
            data = launchParams.data
            mimeType = launchParams.mimeType
            categories = integerListSerializer.serialize(launchParams.categories)
            flags = integerListSerializer.serialize(launchParams.flags)
            extras = extrasSerializer.serialize(launchParams.extras)
        }
    }
}
