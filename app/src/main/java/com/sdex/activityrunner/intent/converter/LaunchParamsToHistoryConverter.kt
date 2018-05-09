package com.sdex.activityrunner.intent.converter

import com.sdex.activityrunner.db.history.HistoryModel
import com.sdex.activityrunner.intent.LaunchParams

class LaunchParamsToHistoryConverter(private val launchParams: LaunchParams) : Converter<HistoryModel> {

  override fun convert(): HistoryModel {
    val integerListSerializer = IntegerListSerializer()
    val extrasSerializer = ExtrasSerializer()
    val historyModel = HistoryModel()
    historyModel.timestamp = System.currentTimeMillis()
    historyModel.packageName = launchParams.packageName
    historyModel.className = launchParams.className
    historyModel.action = launchParams.action
    historyModel.data = launchParams.data
    historyModel.mimeType = launchParams.mimeType
    historyModel.categories = integerListSerializer.serialize(launchParams.categories)
    historyModel.flags = integerListSerializer.serialize(launchParams.flags)
    historyModel.extras = extrasSerializer.serialize(launchParams.extras)
    return historyModel
  }
}
