package com.sdex.activityrunner.intent.converter

import com.sdex.activityrunner.db.history.HistoryModel
import com.sdex.activityrunner.intent.LaunchParams

class HistoryToLaunchParamsConverter(
    private val historyModel: HistoryModel
) : Converter<LaunchParams> {

    override fun convert(): LaunchParams {
        val integerListSerializer = IntegerListSerializer()
        val extrasSerializer = ExtrasSerializer()
        return LaunchParams().apply {
            packageName = historyModel.packageName
            className = historyModel.className
            action = historyModel.action
            data = historyModel.data
            mimeType = historyModel.mimeType
            categories = integerListSerializer.deserialize(historyModel.categories)
            flags = integerListSerializer.deserialize(historyModel.flags)
            extras = extrasSerializer.deserialize(historyModel.extras)
        }
    }
}
