package com.sdex.activityrunner.shortcut

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.maltaisn.icondialog.IconDialog
import com.maltaisn.icondialog.IconDialogSettings
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconDrawableLoader
import com.maltaisn.icondialog.pack.IconPack
import com.maltaisn.icondialog.pack.IconPackLoader
import com.maltaisn.iconpack.mdi.createMaterialDesignIconPack
import com.sdex.activityrunner.R
import com.sdex.activityrunner.app.ActivityModel
import com.sdex.activityrunner.databinding.ActivityAddShortcutBinding
import com.sdex.activityrunner.db.history.HistoryModel
import com.sdex.activityrunner.extensions.doAfterMeasure
import com.sdex.activityrunner.extensions.resolveColorAttr
import com.sdex.activityrunner.extensions.serializable
import com.sdex.activityrunner.intent.converter.HistoryToLaunchParamsConverter
import com.sdex.activityrunner.intent.converter.LaunchParamsToIntentConverter
import com.sdex.activityrunner.preferences.TooltipPreferences
import com.sdex.activityrunner.util.IntentUtils
import com.tomergoldst.tooltips.ToolTip
import com.tomergoldst.tooltips.ToolTipsManager
import kotlin.properties.Delegates

class AddShortcutDialogActivity : AppCompatActivity(), IconDialog.Callback {

    private lateinit var binding: ActivityAddShortcutBinding

    private val toolTipsManager = ToolTipsManager()
    private val pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
        uri?.let { loadIcon(uri) }
    }
    private var launcherLargeIconSize by Delegates.notNull<Int>()
    private var bitmap: Bitmap? = null
    private var iconPack: IconPack? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddShortcutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(this)
        binding.root.background = materialAlertDialogBuilder.background

        val activityModel = intent?.serializable<ActivityModel>(ARG_ACTIVITY_MODEL)
        val historyModel = intent?.serializable<HistoryModel>(ARG_HISTORY_MODEL)

        val loader = IconPackLoader(applicationContext)
        iconPack = createMaterialDesignIconPack(loader)
        iconPack?.loadDrawables(loader.drawableLoader)

        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        launcherLargeIconSize = activityManager.launcherLargeIconSize

        if (activityModel != null) {
            Glide.with(this)
                .load(activityModel)
                .error(R.mipmap.ic_launcher)
                .apply(RequestOptions().centerCrop())
                .override(launcherLargeIconSize)
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?,
                    ) {
                        bitmap = resource.toBitmap()
                        binding.icon.setImageDrawable(resource)
                        showTooltip()
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
            binding.useRoot.isVisible = true
            binding.useRoot.isChecked = !activityModel.exported

            binding.label.doOnTextChanged { _, _, _, count ->
                binding.valueLayout.endIconMode = if (count == 0) {
                    TextInputLayout.END_ICON_DROPDOWN_MENU
                } else {
                    TextInputLayout.END_ICON_CLEAR_TEXT
                }
            }
            binding.label.setText(activityModel.label)
            binding.label.text?.let { binding.label.setSelection(it.length) }
            binding.label.setSimpleItems(
                setOf(activityModel.label, activityModel.name).filterNotNull().toTypedArray()
            )
        }

        if (historyModel != null) {
            binding.label.setText(historyModel.name)
            binding.icon.setImageResource(R.mipmap.ic_launcher)
            binding.valueLayout.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
        }

        binding.icon.setOnClickListener {
            toolTipsManager.dismissAll()
            showIconMenu(it)
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
                IntentUtils.createLauncherIcon(
                    this,
                    model,
                    bitmap,
                    binding.useRoot.isChecked
                )
            }

            historyModel?.let {
                createHistoryModelShortcut(
                    historyModel,
                    shortcutName
                )
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
                    binding.icon,
                    binding.content,
                    getString(R.string.shortcut_set_icon_tooltip),
                    ToolTip.POSITION_BELOW
                )
                builder.setBackgroundColor(
                    resolveColorAttr(
                        com.google.android.material.R.attr.colorTertiary
                    )
                )
                builder.setTextAppearance(R.style.TooltipTextAppearance)
                toolTipsManager.show(builder.build())
                preferences.showChangeIcon = false
            }
        }
    }

    override val iconDialogIconPack: IconPack?
        get() = iconPack

    override fun onIconDialogIconsSelected(dialog: IconDialog, icons: List<Icon>) {
        val icon = icons.first()
        IconDrawableLoader(this).loadDrawable(icon)
        val resource = icon.drawable
        bitmap = resource?.toBitmap(width = launcherLargeIconSize, height = launcherLargeIconSize)
        binding.icon.setImageDrawable(resource)
    }

    private fun showIconMenu(it: View) {
        val popupMenu = PopupMenu(this, it)
        popupMenu.inflate(R.menu.shortcut_icon)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.pick_gallery) {
                pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
            } else if (menuItem.itemId == R.id.pick_icon) {
                if (iconPack != null) {
                    val iconDialog = IconDialog.newInstance(IconDialogSettings {
                        showSelectBtn = false
                    })
                    iconDialog.show(supportFragmentManager, ICON_DIALOG_TAG)
                } else {
                    Toast.makeText(
                        this,
                        R.string.icons_loading_error,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            true
        }
        popupMenu.show()
    }

    private fun createHistoryModelShortcut(historyModel: HistoryModel, shortcutName: String) {
        val historyToLaunchParamsConverter = HistoryToLaunchParamsConverter(historyModel)
        val launchParams = historyToLaunchParamsConverter.convert()
        val converter = LaunchParamsToIntentConverter(launchParams)
        val intent = converter.convert()
        createShortcut(this, shortcutName, intent, bitmap)
    }

    private fun loadIcon(uri: Uri) {
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val size = am.launcherLargeIconSize
        Glide.with(this)
            .asBitmap()
            .load(uri)
            .error(R.mipmap.ic_launcher)
            .apply(RequestOptions().centerCrop().override(size))
            .into(object : CustomTarget<Bitmap>(size, size) {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    bitmap = resource
                    binding.icon.setImageBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    companion object {

        private const val ARG_ACTIVITY_MODEL = "arg_activity_model"
        private const val ARG_HISTORY_MODEL = "arg_history_model"
        private const val ICON_DIALOG_TAG = "icons_dialog"

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
