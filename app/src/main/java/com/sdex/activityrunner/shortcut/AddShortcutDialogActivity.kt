package com.sdex.activityrunner.shortcut

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View.*
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.sdex.activityrunner.R
import com.sdex.activityrunner.app.ActivityModel
import com.sdex.activityrunner.db.history.HistoryModel
import com.sdex.activityrunner.extensions.doAfterMeasure
import com.sdex.activityrunner.glide.GlideApp
import com.sdex.activityrunner.intent.converter.HistoryToLaunchParamsConverter
import com.sdex.activityrunner.intent.converter.LaunchParamsToIntentConverter
import com.sdex.activityrunner.preferences.AdvancedPreferences
import com.sdex.activityrunner.preferences.TooltipPreferences
import com.sdex.activityrunner.util.IntentUtils
import com.sdex.commons.content.ContentManager
import com.sdex.commons.util.ThemeHelper
import com.tomergoldst.tooltips.ToolTip
import com.tomergoldst.tooltips.ToolTipsManager
import kotlinx.android.synthetic.main.activity_add_shortcut.*

class AddShortcutDialogActivity : AppCompatActivity(), ContentManager.PickContentListener {

  private val advancedPreferences: AdvancedPreferences by lazy {
    AdvancedPreferences(PreferenceManager.getDefaultSharedPreferences(this))
  }
  private var contentManager: ContentManager? = null
  private var bitmap: Bitmap? = null
  private val toolTipsManager = ToolTipsManager()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val themeHelper = ThemeHelper()
    themeHelper.setDialogTheme(this, advancedPreferences.getTheme)
    setContentView(R.layout.activity_add_shortcut)

    val activityModel = intent?.getSerializableExtra(ARG_ACTIVITY_MODEL) as ActivityModel?
    val historyModel = intent?.getSerializableExtra(ARG_HISTORY_MODEL) as HistoryModel?

    label.setText(activityModel?.name)
    label.setSelection(label.text.length)

    GlideApp.with(this)
      .load(activityModel)
      .error(R.mipmap.ic_launcher)
      .apply(RequestOptions().centerCrop())
      .into(object : SimpleTarget<Drawable>() {
        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
          bitmap = resource.toBitmap()
          icon.setImageDrawable(resource)
          showTooltip()
        }
      })

    contentManager = ContentManager(this, this)

    icon.setOnClickListener {
      toolTipsManager.dismissAll()
      contentManager?.pickContent(ContentManager.Content.IMAGE)
    }

    cancel.setOnClickListener {
      finish()
    }

    create.setOnClickListener {
      value_layout.error = null
      val shortcutName = label.text.toString()
      if (shortcutName.isBlank()) {
        value_layout.error = getString(R.string.shortcut_name_empty)
        return@setOnClickListener
      }
      if (bitmap != null) {
        activityModel?.let {
          IntentUtils.createLauncherIcon(this, activityModel, bitmap!!)
        }
      } else {
        activityModel?.let {
          activityModel.name = shortcutName
          IntentUtils.createLauncherIcon(this, activityModel)
        }
        historyModel?.let {
          createHistoryModelShortcut(historyModel, shortcutName)
        }
      }

      finish()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    bitmap = null
  }

  @Suppress("DEPRECATION")
  private fun showTooltip() {
    val preferences = TooltipPreferences(this)
    if (preferences.showChangeIcon) {
      icon.doAfterMeasure {
        val builder = ToolTip.Builder(this@AddShortcutDialogActivity,
          icon, content, "Tap to change the icon", ToolTip.POSITION_BELOW)
        builder.setBackgroundColor(resources.getColor(R.color.colorAccent))
        builder.setTextAppearance(R.style.TooltipTextAppearance)
        toolTipsManager.show(builder.build())
        preferences.showChangeIcon = false
      }
    }
  }

  private fun createHistoryModelShortcut(historyModel: HistoryModel, shortcutName: String) {
    val historyToLaunchParamsConverter = HistoryToLaunchParamsConverter(historyModel)
    val launchParams = historyToLaunchParamsConverter.convert()
    val converter = LaunchParamsToIntentConverter(launchParams)
    val intent = converter.convert()
    IntentUtils.createLauncherIcon(this, shortcutName, intent, R.mipmap.ic_launcher)
  }

  override fun onSaveInstanceState(outState: Bundle?) {
    super.onSaveInstanceState(outState)
    contentManager?.onSaveInstanceState(outState)
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
    super.onRestoreInstanceState(savedInstanceState)
    contentManager?.onRestoreInstanceState(savedInstanceState)
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                          grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    contentManager?.onRequestPermissionsResult(requestCode, permissions, grantResults)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    contentManager?.onActivityResult(requestCode, resultCode, data)
  }

  override fun onContentLoaded(uri: Uri?, contentType: String?) {
    uri?.let { loadIcon(uri) }
  }

  private fun loadIcon(uri: Uri) {
    val am: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val size = am.launcherLargeIconSize
    GlideApp.with(this)
      .asBitmap()
      .load(uri)
      .error(R.mipmap.ic_launcher)
      .apply(RequestOptions().centerCrop().override(size))
      .into(object : SimpleTarget<Bitmap>(size, size) {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
          bitmap = resource
          icon.setImageBitmap(resource)
          hideProgress()
        }
      })
  }

  override fun onStartContentLoading() {
    progress.visibility = VISIBLE
    icon.visibility = INVISIBLE
  }

  override fun onError(error: String?) {
    Log.e(TAG, "Failed to load image: $error")
    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
    hideProgress()
  }

  override fun onCanceled() {
    hideProgress()
  }

  private fun hideProgress() {
    progress.visibility = GONE
    icon.visibility = VISIBLE
  }

  companion object {

    private const val TAG = "AddShortcutDialog"

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
