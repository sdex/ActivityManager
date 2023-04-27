package com.sdex.activityrunner.intent.dialog.source

import com.sdex.activityrunner.intent.param.MimeType

class MimeTypeSource : SelectionDialogSource {

    override val list = MimeType.list()

    override fun getItem(position: Int): String = list[position]
}
