package com.sdex.activityrunner.intent.converter

import com.sdex.activityrunner.db.history.HistoryModel
import com.sdex.activityrunner.intent.LaunchParams

class HistoryToLaunchParamsConverter(private val historyModel: HistoryModel) : Converter<LaunchParams> {

  override fun convert(): LaunchParams {
    val integerListSerializer = IntegerListSerializer()
    val extrasSerializer = ExtrasSerializer()
    val params = LaunchParams()
    params.packageName = historyModel.packageName
    params.className = historyModel.className
    params.action = historyModel.action
    params.data = historyModel.data
    params.mimeType = historyModel.mimeType
    params.categories = integerListSerializer.deserialize(historyModel.categories)
    params.flags = integerListSerializer.deserialize(historyModel.flags)
    params.extras = extrasSerializer.deserialize(historyModel.extras)
    return params
  }
}
