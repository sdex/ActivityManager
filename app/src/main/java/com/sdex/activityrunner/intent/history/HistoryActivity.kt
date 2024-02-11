package com.sdex.activityrunner.intent.history

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sdex.activityrunner.R
import com.sdex.activityrunner.commons.BaseActivity
import com.sdex.activityrunner.databinding.ActivityHistoryBinding
import com.sdex.activityrunner.db.history.HistoryModel
import com.sdex.activityrunner.extensions.addDividerItemDecoration
import com.sdex.activityrunner.intent.IntentBuilderActivity
import com.sdex.activityrunner.intent.converter.HistoryToLaunchParamsConverter
import com.sdex.activityrunner.intent.dialog.ExportIntentAsUriDialog
import com.sdex.activityrunner.intent.history.HistoryListAdapter.Companion.MENU_ITEM_ADD_SHORTCUT
import com.sdex.activityrunner.intent.history.HistoryListAdapter.Companion.MENU_ITEM_EXPORT_URI
import com.sdex.activityrunner.intent.history.HistoryListAdapter.Companion.MENU_ITEM_REMOVE
import com.sdex.activityrunner.shortcut.AddShortcutDialogActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryActivity : BaseActivity(), HistoryListAdapter.Callback {

    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var adapter: HistoryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(isBackButtonEnabled = true)

        adapter = HistoryListAdapter(this)
        adapter.setHasStableIds(true)
        binding.list.addDividerItemDecoration()
        binding.list.setHasFixedSize(true)
        binding.list.adapter = adapter
        registerForContextMenu(binding.list)

        viewModel.list.observe(this) {
            adapter.submitList(it)
            it?.let {
                val size = it.size
                subTitle = resources.getQuantityString(R.plurals.history_records, size, size)
                binding.empty.isVisible = (size == 0)
            }
        }

        binding.finish.setOnClickListener {
            val intent = Intent(this, IntentBuilderActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        val position = adapter.contextMenuItemPosition
        adapter.currentList[position]?.let { historyModel ->
            when (itemId) {
                MENU_ITEM_REMOVE -> viewModel.deleteItem(historyModel)
                MENU_ITEM_ADD_SHORTCUT -> showShortcutDialog(historyModel)
                MENU_ITEM_EXPORT_URI -> showExportUriDialog(historyModel)
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun showExportUriDialog(historyModel: HistoryModel) {
        val converter = HistoryToLaunchParamsConverter(historyModel)
        val launchParams = converter.convert()
        ExportIntentAsUriDialog.newInstance(launchParams)
            .show(supportFragmentManager, ExportIntentAsUriDialog.TAG)
    }

    private fun showShortcutDialog(historyModel: HistoryModel) {
        AddShortcutDialogActivity.start(this, historyModel)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.history, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_clear_history -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.history_dialog_clear_title)
                    .setMessage(R.string.history_dialog_clear_message)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        viewModel.clear()
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClicked(item: HistoryModel, position: Int) {
        val historyToLaunchParamsConverter = HistoryToLaunchParamsConverter(item)
        val launchParams = historyToLaunchParamsConverter.convert()
        val data = Intent()
        data.putExtra(RESULT, launchParams)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    companion object {

        const val RESULT = "result"

        fun getLaunchIntent(context: Context): Intent {
            return Intent(context, HistoryActivity::class.java)
        }
    }
}
