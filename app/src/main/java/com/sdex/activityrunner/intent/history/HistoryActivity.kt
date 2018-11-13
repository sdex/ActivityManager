package com.sdex.activityrunner.intent.history

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.view.View.VISIBLE
import com.sdex.activityrunner.R
import com.sdex.activityrunner.db.history.HistoryModel
import com.sdex.activityrunner.extensions.addDivider
import com.sdex.activityrunner.extensions.enableBackButton
import com.sdex.activityrunner.intent.IntentBuilderActivity
import com.sdex.activityrunner.intent.converter.HistoryToLaunchParamsConverter
import com.sdex.activityrunner.intent.dialog.ExportIntentAsUriDialog
import com.sdex.activityrunner.intent.history.HistoryListAdapter.Companion.MENU_ITEM_ADD_SHORTCUT
import com.sdex.activityrunner.intent.history.HistoryListAdapter.Companion.MENU_ITEM_EXPORT_URI
import com.sdex.activityrunner.intent.history.HistoryListAdapter.Companion.MENU_ITEM_REMOVE
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.premium.GetPremiumDialog
import com.sdex.activityrunner.shortcut.AddShortcutDialogActivity
import com.sdex.commons.BaseActivity
import kotlinx.android.synthetic.main.activity_history.*
import kotlin.properties.Delegates

class HistoryActivity : BaseActivity(), HistoryListAdapter.Callback {

  private var appPreferences: AppPreferences by Delegates.notNull()
  private var adapter: HistoryListAdapter by Delegates.notNull()

  private val viewModel: HistoryViewModel by lazy {
    ViewModelProviders.of(this).get(HistoryViewModel::class.java)
  }

  override fun getLayout(): Int {
    return R.layout.activity_history
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    appPreferences = AppPreferences(this)

    enableBackButton()

    adapter = HistoryListAdapter(this)
    adapter.setHasStableIds(true)
    list.addDivider()
    list.setHasFixedSize(true)
    list.adapter = adapter
    registerForContextMenu(list)

    viewModel.list.observe(this, Observer {
      val size = it!!.size
      val subtitle = resources.getQuantityString(R.plurals.history_records, size, size)
      setSubtitle(subtitle)
      adapter.submitList(it)
      val historyWarningShown = appPreferences.isHistoryWarningShown
      if (size == HistoryViewModel.MAX_FREE_RECORDS &&
        !appPreferences.isProVersion && !historyWarningShown) {
        appPreferences.isHistoryWarningShown = true
        val dialog = GetPremiumDialog.newInstance(R.string.pro_version_unlock_history)
        dialog.show(supportFragmentManager, GetPremiumDialog.TAG)
      }
      if (size == 0) {
        empty.visibility = VISIBLE
      }
    })

    finish.setOnClickListener {
      val intent = Intent(this, IntentBuilderActivity::class.java)
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
      startActivity(intent)
      finish()
    }
  }

  override fun onContextItemSelected(item: MenuItem): Boolean {
    val itemId = item.itemId
    val position = adapter.contextMenuItemPosition
    val historyModel = adapter.getItem(position)
    if (historyModel != null) {
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
    val dialog = ExportIntentAsUriDialog.newInstance(launchParams)
    dialog.show(supportFragmentManager, ExportIntentAsUriDialog.TAG)
  }

  private fun showShortcutDialog(historyModel: HistoryModel) {
    if (appPreferences.isProVersion) {
//      val dialog = AddShortcutDialogFragment.newInstance(historyModel)
//      dialog.show(supportFragmentManager, AddShortcutDialogFragment.TAG)
      AddShortcutDialogActivity.start(this, historyModel)
    } else {
      val dialog = GetPremiumDialog.newInstance(R.string.pro_version_unlock_intent_shortcuts)
      dialog.show(supportFragmentManager, GetPremiumDialog.TAG)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.history, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.action_clear_history -> {
        AlertDialog.Builder(this)
          .setTitle(R.string.history_dialog_clear_title)
          .setMessage(R.string.history_dialog_clear_message)
          .setPositiveButton(android.R.string.yes) { _, _ ->
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

    const val REQUEST_CODE = 111

    fun getLaunchIntent(context: Context): Intent {
      return Intent(context, HistoryActivity::class.java)
    }
  }
}
