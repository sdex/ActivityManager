package com.sdex.activityrunner.app

import android.content.ComponentName
import java.io.Serializable

data class ActivityModel(
    val name: String,
    val packageName: String,
    val className: String,
    val label: String?,
    var exported: Boolean,
    var enabled: Boolean,
) : Serializable {

    val componentName: ComponentName
        get() = ComponentName(packageName, className)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as ActivityModel?
        return if (packageName != that?.packageName) false else className == that.className
    }

    override fun hashCode(): Int {
        var result = packageName.hashCode()
        result = 31 * result + className.hashCode()
        return result
    }
}
