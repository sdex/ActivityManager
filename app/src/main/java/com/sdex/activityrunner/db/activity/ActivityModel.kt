package com.sdex.activityrunner.db.activity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.Index
import android.content.ComponentName

import com.sdex.activityrunner.db.application.ApplicationModel

import java.io.Serializable

@Entity(primaryKeys = arrayOf("className"),
  indices = [(Index(value = arrayOf("packageName")))],
  foreignKeys = [ForeignKey(entity = ApplicationModel::class,
    parentColumns = arrayOf("packageName"),
    childColumns = arrayOf("packageName"),
    onDelete = ForeignKey.CASCADE)])
class ActivityModel @Deprecated("")
constructor(var name: String, var packageName: String,
            var className: String, @Deprecated("")
            @get:Deprecated("")
            @set:Deprecated("")
            var iconPath: String, var exported: Boolean) : Serializable {

  val componentName: ComponentName
    get() = ComponentName(packageName, className)

  @Ignore
  constructor(name: String, packageName: String,
              className: String, exported: Boolean)
    : this(name, packageName, className, "", exported)

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
