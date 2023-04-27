package com.sdex.activityrunner.intent.dialog.source

import com.sdex.activityrunner.intent.param.Category

class CategoriesSource : SelectionDialogSource {

    override val list = Category.list()

    override fun getItem(position: Int): String = list[position]
}
