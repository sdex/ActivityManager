package com.sdex.activityrunner.intent.dialog.source

interface SelectionDialogSource {

    val list: List<String>

    fun getItem(position: Int): String
}
