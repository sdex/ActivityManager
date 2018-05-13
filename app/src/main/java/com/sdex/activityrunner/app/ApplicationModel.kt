package com.sdex.activityrunner.app

import java.io.Serializable

class ApplicationModel
constructor(val name: String, val packageName: String) : Serializable {

  var activitiesCount: Int = 0
  var exportedActivitiesCount: Int = 0

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || javaClass != other.javaClass) return false
    val that = other as ApplicationModel?
    return packageName == that!!.packageName
  }

  override fun hashCode(): Int {
    return packageName.hashCode()
  }

  companion object {

    const val TABLE = "ApplicationModel"
    const val NAME = "name"
    const val PACKAGE_NAME = "packageName"
    const val ACTIVITIES_COUNT = "activitiesCount"
  }
}
