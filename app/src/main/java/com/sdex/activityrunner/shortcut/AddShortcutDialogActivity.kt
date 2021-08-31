package com.sdex.activityrunner.shortcut

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.sdex.activityrunner.R
import com.sdex.activityrunner.app.ActivityModel
import com.sdex.activityrunner.databinding.ActivityAddShortcutBinding
import com.sdex.activityrunner.db.history.HistoryModel
import com.sdex.activityrunner.extensions.doAfterMeasure
import com.sdex.activityrunner.glide.GlideApp
import com.sdex.activityrunner.intent.converter.HistoryToLaunchParamsConverter
import com.sdex.activityrunner.intent.converter.LaunchParamsToIntentConverter
import com.sdex.activityrunner.preferences.TooltipPreferences
import com.sdex.activityrunner.util.IntentUtils
import com.sdex.commons.content.ContentManager
import com.tomergoldst.tooltips.ToolTip
import com.tomergoldst.tooltips.ToolTipsManager
import timber.log.Timber

class AddShortcutDialogActivity : AppCompatActivity(), ContentManager.PickContentListener {

    private lateinit var binding: ActivityAddShortcutBinding

    private var contentManager: ContentManager? = null
    private var bitmap: Bitmap? = null
    private val toolTipsManager = ToolTipsManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddShortcutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val activityModel = intent?.getSerializableExtra(ARG_ACTIVITY_MODEL) as ActivityModel?
        val historyModel = intent?.getSerializableExtra(ARG_HISTORY_MODEL) as HistoryModel?

        binding.label.setText(activityModel?.name)
        binding.label.text?.let { binding.label.setSelection(it.length) }

        if (activityModel != null) {
            GlideApp.with(this)
                .load(activityModel)
                .error(R.mipmap.ic_launcher)
                .apply(RequestOptions().centerCrop())
                .into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        bitmap = resource.toBitmap()
                        binding.icon.setImageDrawable(resource)
                        showTooltip()
                    }
                })
        }

        if (historyModel != null) {
            binding.icon.setImageResource(R.mipmap.ic_launcher)
        }

        contentManager = ContentManager(this, this)

        binding.icon.setOnClickListener {
            toolTipsManager.dismissAll()
            if (activityModel != null) {
                contentManager?.pickContent(ContentManager.Content.IMAGE)
            } else {
                Toast.makeText(this, R.string.error_intent_shortcut_icon, Toast.LENGTH_LONG).show()
            }
        }

        binding.cancel.setOnClickListener {
            finish()
        }

        binding.create.setOnClickListener {
            binding.valueLayout.error = null
            val shortcutName = binding.label.text.toString()
            if (shortcutName.isBlank()) {
                binding.valueLayout.error = getString(R.string.shortcut_name_empty)
                return@setOnClickListener
            }

            activityModel?.let {
                val model = it.copy(name = shortcutName)
                if (bitmap != null) {
                    IntentUtils.createLauncherIcon(this, model, bitmap!!)
                } else {
                    IntentUtils.createLauncherIcon(this, model)
                }
            }

            historyModel?.let {
                createHistoryModelShortcut(historyModel, shortcutName)
            }

            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bitmap = null
    }

    private fun showTooltip() {
        val preferences = TooltipPreferences(this)
        if (preferences.showChangeIcon) {
            binding.icon.doAfterMeasure {
                val builder = ToolTip.Builder(
                    this@AddShortcutDialogActivity,
                    binding.icon, binding.content,
                    "Tap to change the icon", ToolTip.POSITION_BELOW
                )
                builder.setBackgroundColor(
                    ContextCompat.getColor(
                        this@AddShortcutDialogActivity, R.color.colorAccent
                    )
                )
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        contentManager?.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        contentManager?.onRestoreInstanceState(savedInstanceState)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
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
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val size = am.launcherLargeIconSize
        GlideApp.with(this)
            .asBitmap()
            .load(uri)
            .error(R.mipmap.ic_launcher)
            .apply(RequestOptions().centerCrop().override(size))
            .into(object : SimpleTarget<Bitmap>(size, size) {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    bitmap = resource
                    binding.icon.setImageBitmap(resource)
                    hideProgress()
                }
            })
    }

    override fun onStartContentLoading() {
        binding.progress.isVisible = true
        binding.icon.isInvisible = true
    }

    override fun onError(error: String?) {
        Timber.e("Failed to load image: %s", error)
        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
        hideProgress()
    }

    override fun onCanceled() {
        hideProgress()
    }

    private fun hideProgress() {
        binding.progress.isVisible = false
        binding.icon.isVisible = true
    }

    companion object {

        private const val ARG_ACTIVITY_MODEL = "arg_activity_model"
        private const val ARG_HISTORY_MODEL = "arg_history_model"

        fun start(context: Context, activityModel: ActivityModel) {
            context.startActivity(
                Intent(context, AddShortcutDialogActivity::class.java).apply {
                    putExtra(ARG_ACTIVITY_MODEL, activityModel)
                }
            )
        }

        fun start(context: Context, historyModel: HistoryModel) {
            context.startActivity(Intent(context, AddShortcutDialogActivity::class.java).apply {
                putExtra(ARG_HISTORY_MODEL, historyModel)
            })
        }
    }
}
