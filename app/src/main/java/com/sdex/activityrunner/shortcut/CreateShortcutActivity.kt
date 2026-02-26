package com.sdex.activityrunner.shortcut

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
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
import com.sdex.activityrunner.extensions.serializable
import com.sdex.activityrunner.intent.converter.HistoryToLaunchParamsConverter
import com.sdex.activityrunner.intent.converter.LaunchParamsToIntentConverter
import com.sdex.activityrunner.util.IntentUtils
import kotlin.properties.Delegates

class CreateShortcutActivity : AppCompatActivity(), IconDialog.Callback {

    private lateinit var binding: ActivityAddShortcutBinding

    private val pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
        uri?.let { loadIcon(uri) }
    }
    private var launcherLargeIconSize by Delegates.notNull<Int>()
    private var bitmap: Bitmap? = null
    private var originalBitmap: Bitmap? = null
    private var iconPack: IconPack? = null
    private var iconPadding: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddShortcutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        val activityModel = intent?.serializable<ActivityModel>(ARG_ACTIVITY_MODEL)
        val historyModel = intent?.serializable<HistoryModel>(ARG_HISTORY_MODEL)

        addMenuProvider(
            AddShortcutMenuProvider(
                activityModel = activityModel,
                historyModel = historyModel,
            ),
        )

        val loader = IconPackLoader(applicationContext)
        iconPack = createMaterialDesignIconPack(loader)
        iconPack?.loadDrawables(loader.drawableLoader)

        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        launcherLargeIconSize = activityManager.launcherLargeIconSize

        if (activityModel != null) {
            Glide.with(this)
                .load(activityModel)
                .error(R.mipmap.ic_launcher)
                .apply(RequestOptions().centerCrop())
                .override(launcherLargeIconSize)
                .into(
                    object : CustomTarget<Drawable>() {
                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?,
                        ) {
                            originalBitmap = resource.toBitmap()
                            updateIconPreview()
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    },
                )

            binding.activityInfo.isVisible = true
            binding.className.text = activityModel.componentName.className
            binding.rootWarning.isVisible = !activityModel.exported

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
                setOf(activityModel.label, activityModel.name)
                    .filterNotNull()
                    .toTypedArray(),
            )
        }

        if (historyModel != null) {
            binding.label.setText(historyModel.name)
            binding.valueLayout.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT

            originalBitmap = ContextCompat.getDrawable(
                this,
                R.mipmap.ic_launcher,
            )?.toBitmapOrNull()

            updateIconPreview()
        }

        binding.invertIconColors.setOnCheckedChangeListener { _, _ ->
            updateIconPreview()
        }

        binding.iconPaddingSlider.addOnChangeListener { _, value, _ ->
            iconPadding = value.toInt()
            updateIconPreview()
        }

        binding.changeIconButton.setOnClickListener {
            showIconMenu(it)
        }
    }

    private fun createShortcut(activityModel: ActivityModel?, historyModel: HistoryModel?) {
        binding.valueLayout.error = null
        val shortcutName = binding.label.text.toString()
        if (shortcutName.isBlank()) {
            binding.valueLayout.error = getString(R.string.shortcut_name_empty)
            return
        }

        activityModel?.let {
            val model = it.copy(name = shortcutName)
            IntentUtils.createLauncherIcon(
                context = this,
                activityModel = model,
                bitmap = bitmap,
            )
        }

        historyModel?.let {
            createHistoryModelShortcut(
                historyModel = historyModel,
                shortcutName = shortcutName,
            )
        }

        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        bitmap = null
        originalBitmap = null
    }

    private fun updateIconPreview() {
        val source = originalBitmap ?: return

        bitmap = source.applyShortcutTweaks(
            invertIconColors = binding.invertIconColors.isChecked,
            iconPadding = iconPadding,
        )

        binding.icon.setImageBitmap(bitmap)
    }

    override val iconDialogIconPack: IconPack?
        get() = iconPack

    override fun onIconDialogIconsSelected(dialog: IconDialog, icons: List<Icon>) {
        val icon = icons.first()
        IconDrawableLoader(this).loadDrawable(icon)
        val resource = icon.drawable
        originalBitmap = resource?.toBitmap(
            width = launcherLargeIconSize,
            height = launcherLargeIconSize,
        )

        updateIconPreview()
    }

    private fun showIconMenu(it: View) {
        val popupMenu = PopupMenu(this, it)
        popupMenu.inflate(R.menu.shortcut_icon)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.pick_gallery) {
                pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
            } else if (menuItem.itemId == R.id.pick_icon) {
                if (iconPack != null) {
                    val iconDialog = IconDialog.newInstance(
                        IconDialogSettings {
                            showSelectBtn = false
                        },
                    )
                    iconDialog.show(supportFragmentManager, ICON_DIALOG_TAG)
                } else {
                    Toast.makeText(
                        this,
                        R.string.icons_loading_error,
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
            true
        }
        popupMenu.show()
    }

    private fun createHistoryModelShortcut(
        historyModel: HistoryModel,
        shortcutName: String,
    ) {
        val historyToLaunchParamsConverter = HistoryToLaunchParamsConverter(historyModel)
        val launchParams = historyToLaunchParamsConverter.convert()
        val converter = LaunchParamsToIntentConverter(launchParams)
        val intent = converter.convert()

        createShortcut(
            context = this,
            name = shortcutName,
            intent = intent,
            icon = bitmap,
        )
    }

    private fun loadIcon(uri: Uri) {
        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val size = am.launcherLargeIconSize
        Glide.with(this)
            .asBitmap()
            .load(uri)
            .error(R.mipmap.ic_launcher)
            .apply(RequestOptions().centerCrop().override(size))
            .into(
                object : CustomTarget<Bitmap>(size, size) {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?,
                    ) {
                        originalBitmap = resource
                        updateIconPreview()
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                },
            )
    }

    private fun Bitmap.applyShortcutTweaks(
        invertIconColors: Boolean,
        iconPadding: Int,
    ): Bitmap {
        val paint = Paint().apply {
            if (invertIconColors) {
                colorFilter = ShortcutColorInvertColorFilter()
            }
        }

        val paddedWidth = width + iconPadding
        val paddedHeight = height + iconPadding
        val tweakedIcon = createBitmap(
            width = paddedWidth,
            height = paddedHeight,
            config = config ?: Bitmap.Config.ARGB_8888,
        )
        val canvas = Canvas(tweakedIcon)
        canvas.drawBitmap(
            this,
            (paddedWidth - width) / 2f,
            (paddedHeight - height) / 2f,
            paint,
        )

        return tweakedIcon
    }


    private inner class AddShortcutMenuProvider(
        private val activityModel: ActivityModel? = null,
        private val historyModel: HistoryModel? = null,
    ) : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.add_shortcut_menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.action_save -> {
                    createShortcut(activityModel, historyModel)
                    true
                }

                else -> false
            }
        }
    }

    companion object {

        private const val ARG_ACTIVITY_MODEL = "arg_activity_model"
        private const val ARG_HISTORY_MODEL = "arg_history_model"
        private const val ICON_DIALOG_TAG = "icons_dialog"

        fun start(context: Context, activityModel: ActivityModel) {
            context.startActivity(
                Intent(context, CreateShortcutActivity::class.java).apply {
                    putExtra(ARG_ACTIVITY_MODEL, activityModel)
                },
            )
        }

        fun start(context: Context, historyModel: HistoryModel) {
            context.startActivity(
                Intent(context, CreateShortcutActivity::class.java).apply {
                    putExtra(ARG_HISTORY_MODEL, historyModel)
                },
            )
        }
    }
}
