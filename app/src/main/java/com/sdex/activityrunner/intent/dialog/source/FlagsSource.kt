package com.sdex.activityrunner.intent.dialog.source

import com.sdex.activityrunner.intent.param.Flag

class FlagsSource : SelectionDialogSource {

    override val list = Flag.list()

    override fun getItem(position: Int): String = list[position]
}
