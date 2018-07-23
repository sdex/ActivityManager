package com.sdex.activityrunner.intent.dialog

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.View
import com.sdex.activityrunner.R
import com.sdex.activityrunner.db.history.HistoryModel
import com.sdex.activityrunner.intent.converter.HistoryToLaunchParamsConverter
import com.sdex.activityrunner.intent.converter.LaunchParamsToWebIntentConverter
import kotlinx.android.synthetic.main.dialog_export_intent_as_uri.view.*

class ExportIntentAsUriDialog : DialogFragment() {

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val historyModel = arguments?.getSerializable(ARG_HISTORY_MODEL) as HistoryModel?

    val builder = AlertDialog.Builder(activity!!)
    val view = View.inflate(activity, R.layout.dialog_export_intent_as_uri, null)

    val historyToLaunchParamsConverter = HistoryToLaunchParamsConverter(historyModel!!)
    val launchParamsToWebIntentConverter =
      LaunchParamsToWebIntentConverter(historyToLaunchParamsConverter.convert())
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

    private const val ARG_HISTORY_MODEL = "arg_history_model"

    fun newInstance(historyModel: HistoryModel): ExportIntentAsUriDialog {
      val args = Bundle(1)
      args.putSerializable(ARG_HISTORY_MODEL, historyModel)
      val fragment = ExportIntentAsUriDialog()
      fragment.arguments = args
      return fragment
    }
  }
}