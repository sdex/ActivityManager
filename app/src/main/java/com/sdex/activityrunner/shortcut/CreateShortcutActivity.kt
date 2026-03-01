package com.sdex.activityrunner.shortcut

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
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
import com.sdex.activityrunner.util.makeShortcutIntent
import kotlin.properties.Delegates

/**
 * Possible icons:
 *  default - R.drawable.bookmark_24px
 *  icon from icon pack
 *  image from gallery
 *  activity icon
 */
class CreateShortcutActivity : AppCompatActivity(), IconDialog.Callback {

    private lateinit var binding: ActivityAddShortcutBinding

    private val pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
        uri?.let { loadIcon(uri) }
    }
    private var launcherLargeIconSize by Delegates.notNull<Int>()
    private var bitmap: Bitmap? = null
    private var originalBitmap: Bitmap? = null
    private var iconPack: IconPack? = null
    private var isAdaptiveBitmap = true

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
            isAdaptiveBitmap = true

            Glide.with(this)
                .load(activityModel)
                .error(R.drawable.bookmark_24px)
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
            val labels = setOf(activityModel.label, activityModel.name)
                .filterNot { it.isNullOrBlank() }
                .toTypedArray()
            binding.label.setText(labels.firstOrNull())
            binding.label.text?.let { binding.label.setSelection(it.length) }
            binding.label.setSimpleItems(labels)
        }

        if (historyModel != null) {
            binding.label.setText(historyModel.name)
            binding.valueLayout.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT

            isAdaptiveBitmap = false
            originalBitmap = ContextCompat.getDrawable(
                this,
                R.drawable.bookmark_24px,
            )?.toBitmapOrNull()

            updateIconPreview()
        }

        binding.invertIconColors.setOnCheckedChangeListener { _, _ ->
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

        val shortcutBitmap = bitmap

        val intent = activityModel?.makeShortcutIntent(context = this)
            ?: historyModel?.makeShortcutIntent()

        intent?.let {
            createShortcut(
                context = this,
                name = shortcutName,
                intent = intent,
                icon = shortcutBitmap,
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
            isAdaptiveBitmap = isAdaptiveBitmap,
            invertIconColors = binding.invertIconColors.isChecked,
        )

        binding.icon.setImageBitmap(bitmap)
    }

    override val iconDialogIconPack: IconPack?
        get() = iconPack

    override fun onIconDialogIconsSelected(dialog: IconDialog, icons: List<Icon>) {
        val icon = icons.first()
        IconDrawableLoader(this).loadDrawable(icon)
        val resource = icon.drawable
        isAdaptiveBitmap = false
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

    private fun loadIcon(uri: Uri) {
        Glide.with(this)
            .asBitmap()
            .load(uri)
            .error(R.drawable.bookmark_24px)
            .apply(
                RequestOptions().centerCrop(),
            )
            .into(
                object : CustomTarget<Bitmap>(
                    launcherLargeIconSize,
                    launcherLargeIconSize,
                ) {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?,
                    ) {
                        isAdaptiveBitmap = true
                        originalBitmap = resource
                        updateIconPreview()
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                },
            )
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
