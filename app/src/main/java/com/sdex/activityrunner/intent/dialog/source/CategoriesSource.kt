package com.sdex.activityrunner.intent.dialog.source

import com.sdex.activityrunner.intent.param.Category
import java.util.*

class CategoriesSource : SelectionDialogSource {

  override val list: ArrayList<String>
    get() = Category.list()

  override fun getItem(position: Int): String {
    return list[position]
  }
}
