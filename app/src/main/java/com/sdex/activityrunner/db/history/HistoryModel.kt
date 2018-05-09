package com.sdex.activityrunner.db.history

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity
class HistoryModel : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var timestamp: Long = 0
    var packageName: String? = null
    var className: String? = null
    var action: Int = 0
    var data: String? = null
    var mimeType: Int = 0
    var categories: String? = null
    var flags: String? = null
    var extras: String? = null
}
