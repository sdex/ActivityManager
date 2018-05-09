package com.sdex.activityrunner.db.application

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore

import java.io.Serializable

@Entity(primaryKeys = ["packageName"])
class ApplicationModel @Deprecated("")
constructor(val name: String, val packageName: String,
            @Deprecated("")
            @get:Deprecated("")
            val iconPath: String) : Serializable {
  var activitiesCount: Int = 0
  var exportedActivitiesCount: Int = 0

  @Ignore
  constructor(name: String, packageName: String) : this(name, packageName, "")

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
    const val EXPORTED_ACTIVITIES_COUNT = "exportedActivitiesCount"
  }
}
