package com.sdex.activityrunner.intent.dialog.source

import java.util.*

interface SelectionDialogSource {

    val list: ArrayList<String>

    fun getItem(position: Int): String
}
