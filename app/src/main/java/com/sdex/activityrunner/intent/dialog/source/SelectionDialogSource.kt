package com.sdex.activityrunner.intent.dialog.source

interface SelectionDialogSource {

    val list: ArrayList<String>

    fun getItem(position: Int): String
}
