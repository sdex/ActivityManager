package com.sdex.activityrunner.intent.dialog.source

import com.sdex.activityrunner.intent.param.Action

class ActionSource : SelectionDialogSource {

    override val list = Action.list()

    override fun getItem(position: Int): String = list[position]
}
