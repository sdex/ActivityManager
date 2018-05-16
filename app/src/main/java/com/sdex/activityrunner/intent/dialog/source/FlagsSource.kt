package com.sdex.activityrunner.intent.dialog.source

import com.sdex.activityrunner.intent.param.Flag
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class FlagsSource : SelectionDialogSource {

  override val list: ArrayList<String>
    get() = Flag.list()

  override fun getItem(position: Int): String {
    return list[position]
  }
}
