package com.sdex.activityrunner.intent.dialog.source

import com.sdex.activityrunner.intent.param.Action
import java.util.*

class ActionSource : SelectionDialogSource {

    override val list: ArrayList<String>
        get() = Action.list()

    override fun getItem(position: Int): String {
        return list[position]
    }
}
