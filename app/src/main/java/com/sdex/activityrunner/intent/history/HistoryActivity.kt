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
import com.sdex.activityrunner.GetPremiumDialog
import com.sdex.activityrunner.R
import com.sdex.activityrunner.db.history.HistoryModel
import com.sdex.activityrunner.intent.converter.HistoryToLaunchParamsConverter
import com.sdex.activityrunner.shortcut.AddShortcutDialogFragment
import com.sdex.activityrunner.util.addDivider
import com.sdex.commons.BaseActivity
import com.sdex.commons.ads.AppPreferences
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : BaseActivity(), HistoryListAdapter.Callback {

  private var appPreferences: AppPreferences? = null
  private var adapter: HistoryListAdapter? = null
  private var viewModel: HistoryViewModel? = null

  override fun getLayout(): Int {
    return R.layout.activity_history
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    viewModel = ViewModelProviders.of(this).get(HistoryViewModel::class.java)
    appPreferences = AppPreferences(this)

    enableBackButton()

    adapter = HistoryListAdapter(this)
    adapter!!.setHasStableIds(true)
    list.addDivider()
    list.setHasFixedSize(true)
    list.adapter = adapter
    registerForContextMenu(list)

    viewModel!!.history.observe(this, Observer {
      val size = it!!.size
      val subtitle = resources.getQuantityString(R.plurals.history_records, size, size)
      setSubtitle(subtitle)
      adapter!!.setItems(it)
      val historyWarningShown = appPreferences!!.isHistoryWarningShown
      if (size == HistoryViewModel.MAX_FREE_RECORDS &&
        !appPreferences!!.isProVersion && !historyWarningShown) {
        appPreferences!!.isHistoryWarningShown = true
        val dialog = GetPremiumDialog.newInstance(R.string.pro_version_unlock_history)
        dialog.show(supportFragmentManager, GetPremiumDialog.TAG)
      }
    })
  }

  override fun onContextItemSelected(item: MenuItem): Boolean {
    val itemId = item.itemId
    if (itemId == HistoryListAdapter.MENU_ITEM_REMOVE) {
      val position = adapter!!.contextMenuItemPosition
      val historyModel = adapter!!.getItem(position)
      viewModel!!.deleteItem(historyModel)
    } else if (itemId == HistoryListAdapter.MENU_ITEM_ADD_SHORTCUT) {
      if (appPreferences!!.isProVersion) {
        val position = adapter!!.contextMenuItemPosition
        val historyModel = adapter!!.getItem(position)
        val dialog = AddShortcutDialogFragment.newInstance(historyModel)
        dialog.show(supportFragmentManager, AddShortcutDialogFragment.TAG)
      } else {
        val dialog = GetPremiumDialog.newInstance(R.string.pro_version_unlock_intent_shortcuts)
        dialog.show(supportFragmentManager, GetPremiumDialog.TAG)
      }
    }
    return super.onContextItemSelected(item)
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
            viewModel!!.clear()
            finish()
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
