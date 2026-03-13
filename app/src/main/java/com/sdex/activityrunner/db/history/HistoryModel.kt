package com.sdex.activityrunner.db.history

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class HistoryModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val name: String?,
    val packageName: String?,
    val className: String?,
    val action: String? = null,
    val data: String? = null,
    val mimeType: String? = null,
    val categories: String? = null,
    val flags: String? = null,
    val extras: String? = null,
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as HistoryModel
        return id == other.id
    }

    override fun hashCode(): Int {
        return id
    }
}
