package com.sdex.activityrunner.shortcut

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.sdex.activityrunner.R
import com.sdex.activityrunner.app.ActivityModel
import com.sdex.activityrunner.db.history.HistoryModel
import com.sdex.activityrunner.glide.GlideApp
import com.sdex.activityrunner.intent.converter.HistoryToLaunchParamsConverter
import com.sdex.activityrunner.intent.converter.LaunchParamsToIntentConverter
import com.sdex.activityrunner.util.IntentUtils
import kotlinx.android.synthetic.main.activity_add_shortcut.*

class AddShortcutDialogActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_add_shortcut)

    val activityModel = intent?.getSerializableExtra(ARG_ACTIVITY_MODEL) as ActivityModel?
    val historyModel = intent?.getSerializableExtra(ARG_HISTORY_MODEL) as HistoryModel?

    label.setText(activityModel?.name)
    label.setSelection(label.text.length)

    GlideApp.with(this)
      .load(activityModel)
      .error(R.mipmap.ic_launcher)
      .apply(RequestOptions()
        .fitCenter())
      .into(object : SimpleTarget<Drawable>() {
        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
          icon.setImageDrawable(resource)
        }
      })

    cancel.setOnClickListener {
      finish()
    }

    create.setOnClickListener { _ ->
      value_layout.error = null
      val shortcutName = label.text.toString()
      if (shortcutName.isBlank()) {
        value_layout.error = getString(R.string.shortcut_name_empty)
        return@setOnClickListener
      }

      activityModel?.let {
        activityModel.name = shortcutName
        IntentUtils.createLauncherIcon(this, activityModel)
      }
      historyModel?.let {
        createHistoryModelShortcut(historyModel, shortcutName)
      }

      finish()
    }
  }

  private fun createHistoryModelShortcut(historyModel: HistoryModel, shortcutName: String) {
    val historyToLaunchParamsConverter = HistoryToLaunchParamsConverter(historyModel)
    val launchParams = historyToLaunchParamsConverter.convert()
    val converter = LaunchParamsToIntentConverter(launchParams)
    val intent = converter.convert()
    IntentUtils.createLauncherIcon(this, shortcutName, intent, R.mipmap.ic_launcher)
  }

  companion object {

    private const val ARG_ACTIVITY_MODEL = "arg_activity_model"
    private const val ARG_HISTORY_MODEL = "arg_history_model"

    fun start(context: Context, activityModel: ActivityModel) {
      val starter = Intent(context, AddShortcutDialogActivity::class.java)
      starter.putExtra(ARG_ACTIVITY_MODEL, activityModel)
      context.startActivity(starter)
    }

    fun start(context: Context, historyModel: HistoryModel) {
      val starter = Intent(context, AddShortcutDialogActivity::class.java)
      starter.putExtra(ARG_HISTORY_MODEL, historyModel)
      context.startActivity(starter)
    }
  }
}