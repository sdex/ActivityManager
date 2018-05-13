package com.sdex.activityrunner.db.activity

import android.content.ComponentName
import java.io.Serializable

class ActivityModel
constructor(var name: String,
            var packageName: String,
            var className: String,
            var exported: Boolean) : Serializable {

  val componentName: ComponentName
    get() = ComponentName(packageName, className)

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || javaClass != other.javaClass) return false

    val that = other as ActivityModel?

    return if (packageName != that!!.packageName) false else className == that.className
  }

  override fun hashCode(): Int {
    var result = packageName.hashCode()
    result = 31 * result + className.hashCode()
    return result
  }
}
