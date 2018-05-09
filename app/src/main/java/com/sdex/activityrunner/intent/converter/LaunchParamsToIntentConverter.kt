package com.sdex.activityrunner.intent.converter

import android.content.Intent
import android.net.Uri
import android.util.Log
import com.sdex.activityrunner.intent.LaunchParams
import com.sdex.activityrunner.intent.LaunchParamsExtra
import com.sdex.activityrunner.intent.LaunchParamsExtraType
import com.sdex.activityrunner.intent.param.Action
import com.sdex.activityrunner.intent.param.Category
import com.sdex.activityrunner.intent.param.Flag
import java.util.*

class LaunchParamsToIntentConverter(private val launchParams: LaunchParams) : Converter<Intent> {

  override fun convert(): Intent {
    val intent = Intent()
    val packageName = launchParams.packageName
    intent.`package` = packageName
    val className = launchParams.className
    if (packageName != null && className != null) {
      intent.setClassName(packageName, className)
    }
    intent.action = Action.getAction(launchParams.actionValue)
    val data = launchParams.data
    if (data != null) {
      intent.data = Uri.parse(data)
    }
    intent.type = launchParams.mimeTypeValue
    val categories = Category.list(launchParams.categoriesValues)
    for (category in categories) {
      intent.addCategory(category)
    }
    val flags = Flag.list(launchParams.flagsValues)
    for (flag in flags) {
      intent.addFlags(flag!!)
    }
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
          LaunchParamsExtraType.LONG -> intent.putExtra(key, java.lang.Long.parseLong(value))
          LaunchParamsExtraType.FLOAT -> intent.putExtra(key, java.lang.Float.parseFloat(value))
          LaunchParamsExtraType.DOUBLE -> intent.putExtra(key, java.lang.Double.parseDouble(value))
          LaunchParamsExtraType.BOOLEAN -> intent.putExtra(key, java.lang.Boolean.parseBoolean(value))
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
