package com.sdex.activityrunner.intent.dialog.source

import android.os.Parcelable
import java.util.*

interface SelectionDialogSource : Parcelable {

  val list: ArrayList<String>

  fun getItem(position: Int): String
}
