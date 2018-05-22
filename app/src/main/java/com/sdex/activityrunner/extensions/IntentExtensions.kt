package com.sdex.activityrunner.extensions

import android.content.Intent

fun Intent.getFlagsList(): List<String> {
  val declaredFields = Intent::class.java.declaredFields
  val list = ArrayList<String>()
  for (field in declaredFields) {
    if (field.name.startsWith("FLAG_")) {
      try {
        val flag = field.getInt(null)
        if (flags and flag != 0) {
          list.add(field.name)
        }
      } catch (e: IllegalArgumentException) {
        e.printStackTrace()
      } catch (e: IllegalAccessException) {
        e.printStackTrace()
      }
    }
  }
  return list
}