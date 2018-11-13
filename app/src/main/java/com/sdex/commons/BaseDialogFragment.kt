package com.sdex.commons

import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager

open class BaseDialogFragment : DialogFragment() {

  override fun show(manager: FragmentManager, tag: String) {
    try {
      super.show(manager, tag)
    } catch (e: IllegalStateException) {
      e.printStackTrace()
    }
  }
}
