package com.sdex.activityrunner.db.cache

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = ApplicationModel.TABLE)
data class ApplicationModel(
    @PrimaryKey val packageName: String,
    val name: String?,
    var activitiesCount: Int = 0,
    var exportedActivitiesCount: Int = 0,
    var system: Boolean = false
) : Serializable {

    companion object {

        const val TABLE = "ApplicationModel"
        const val NAME = "name"
        const val PACKAGE_NAME = "packageName"
        const val ACTIVITIES_COUNT = "activitiesCount"
        const val EXPORTED_ACTIVITIES_COUNT = "exportedActivitiesCount"
    }
}
