package com.sdex.activityrunner.db.history

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class HistoryModel : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var timestamp: Long = 0
    var name: String? = null
    var packageName: String? = null
    var className: String? = null
    var action: String? = null
    var data: String? = null
    var mimeType: String? = null
    var categories: String? = null
    var flags: String? = null
    var extras: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as HistoryModel
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id
    }
}
