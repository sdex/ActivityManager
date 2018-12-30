package com.sdex.activityrunner.intent.dialog

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.sdex.activityrunner.R
import com.sdex.activityrunner.intent.LaunchParams
import com.sdex.activityrunner.intent.converter.LaunchParamsToWebIntentConverter
import com.sdex.commons.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_export_intent_as_uri.view.*

class ExportIntentAsUriDialog : BaseDialogFragment() {

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val launchParams = arguments?.getParcelable(ARG_LAUNCH_PARAMS) as LaunchParams?

    val builder = AlertDialog.Builder(activity!!)
    val view = View.inflate(activity, R.layout.dialog_export_intent_as_uri, null)

    val launchParamsToWebIntentConverter = LaunchParamsToWebIntentConverter(launchParams!!)
    val value = launchParamsToWebIntentConverter.convert()
    view.value.text = value

    builder.setTitle(R.string.history_item_dialog_export_uri)
      .setView(view)
      .setPositiveButton(R.string.dialog_export_intent_copy) { _, _ ->
        val clipboard = context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = ClipData.newPlainText("Intent URI", value)
        clipboard!!.primaryClip = clip
      }
      .setNegativeButton(android.R.string.cancel, null)

    return builder.create()
  }

  companion object {

    const val TAG = "ExportIntentAsUriDialog"

    private const val ARG_LAUNCH_PARAMS = "arg_launch_params"

    fun newInstance(launchParams: LaunchParams): ExportIntentAsUriDialog {
      val args = Bundle(1)
      args.putParcelable(ARG_LAUNCH_PARAMS, launchParams)
      val fragment = ExportIntentAsUriDialog()
      fragment.arguments = args
      return fragment
    }
  }
}
