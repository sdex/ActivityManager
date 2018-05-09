package com.sdex.activityrunner.intent

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.sdex.activityrunner.GetPremiumDialog
import com.sdex.activityrunner.R
import com.sdex.activityrunner.db.activity.ActivityModel
import com.sdex.activityrunner.intent.LaunchParamsExtraListAdapter.Callback
import com.sdex.activityrunner.intent.converter.LaunchParamsToIntentConverter
import com.sdex.activityrunner.intent.dialog.ExtraInputDialog
import com.sdex.activityrunner.intent.dialog.MultiSelectionDialog
import com.sdex.activityrunner.intent.dialog.SingleSelectionDialog
import com.sdex.activityrunner.intent.dialog.ValueInputDialog
import com.sdex.activityrunner.intent.dialog.source.*
import com.sdex.activityrunner.intent.history.HistoryActivity
import com.sdex.activityrunner.util.IntentUtils
import com.sdex.commons.BaseActivity
import com.sdex.commons.ads.AdsDelegate
import com.sdex.commons.ads.AppPreferences
import kotlinx.android.synthetic.main.activity_intent_builder.*
import java.util.*

class IntentBuilderActivity : BaseActivity(),
  ValueInputDialog.OnValueInputDialogCallback, SingleSelectionDialog.OnItemSelectedCallback,
  MultiSelectionDialog.OnItemsSelectedCallback, ExtraInputDialog.OnKeyValueInputDialogCallback {

  private var launchParams: LaunchParams = LaunchParams()

  private var categoriesAdapter: LaunchParamsListAdapter? = null
  private var flagsAdapter: LaunchParamsListAdapter? = null
  private var extraAdapter: LaunchParamsExtraListAdapter? = null
  private var viewModel: LaunchParamsViewModel? = null

  private var appPreferences: AppPreferences? = null
  private var adsDelegate: AdsDelegate? = null

  override fun getLayout(): Int {
    return R.layout.activity_intent_builder
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    viewModel = ViewModelProviders.of(this).get(LaunchParamsViewModel::class.java)

    appPreferences = AppPreferences(this)

    val adsContainer = findViewById<FrameLayout>(R.id.ads_container)
    adsDelegate = AdsDelegate(appPreferences, adsContainer)
    adsDelegate!!.initBanner(this, R.string.ad_banner_unit_id)

    enableBackButton()
    val activityModel = intent.getSerializableExtra(ARG_ACTIVITY_MODEL) as ActivityModel?
    if (activityModel != null) {
      title = activityModel.name
    }
    launchParams.packageName = activityModel?.packageName
    launchParams.className = activityModel?.className

    val params = savedInstanceState?.getParcelable(STATE_LAUNCH_PARAMS) as LaunchParams?
    if (params != null) {
      launchParams = params
    }

    configureRecyclerView(listExtrasView)
    configureRecyclerView(listCategoriesView)
    configureRecyclerView(listFlagsView)

    extraAdapter = LaunchParamsExtraListAdapter(object : Callback {
      override fun onItemSelected(position: Int) {
        val extra = launchParams.extras[position]
        val dialog = ExtraInputDialog.newInstance(extra, position)
        dialog.show(supportFragmentManager, ExtraInputDialog.TAG)
      }

      override fun removeItem(position: Int) {
        launchParams.extras.removeAt(position)
        extraAdapter!!.notifyDataSetChanged()
        listExtrasView.requestLayout()
        updateExtrasAdd()
      }
    })
    extraAdapter!!.setHasStableIds(true)
    listExtrasView.adapter = extraAdapter
    categoriesAdapter = LaunchParamsListAdapter()
    categoriesAdapter!!.setHasStableIds(true)
    listCategoriesView.adapter = categoriesAdapter
    flagsAdapter = LaunchParamsListAdapter()
    flagsAdapter!!.setHasStableIds(true)
    listFlagsView.adapter = flagsAdapter

    bindInputValueDialog(R.id.container_package_name, R.string.launch_param_package_name)
    bindInputValueDialog(R.id.container_class_name, R.string.launch_param_class_name)
    bindInputValueDialog(R.id.container_data, R.string.launch_param_data)
    bindSingleSelectionDialog(R.id.container_action, R.string.launch_param_action,
      ActionSource())
    bindSingleSelectionDialog(R.id.container_mime_type, R.string.launch_param_mime_type,
      MimeTypeSource())
    bindKeyValueDialog(R.id.container_extras)
    bindMultiSelectionDialog(R.id.categories_click_interceptor, R.string.launch_param_categories,
      CategoriesSource())
    bindMultiSelectionDialog(R.id.flags_click_interceptor, R.string.launch_param_flags,
      FlagsSource())

    findViewById<View>(R.id.launch).setOnClickListener {
      viewModel!!.addToHistory(launchParams)
      val converter = LaunchParamsToIntentConverter(launchParams)
      val intent = converter.convert()
      IntentUtils.launchActivity(this@IntentBuilderActivity, intent)
    }

    showLaunchParams()
  }

  override fun onResume() {
    super.onResume()
    adsDelegate!!.detachBottomBannerIfNeed()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putParcelable(STATE_LAUNCH_PARAMS, launchParams)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == HistoryActivity.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
      val result = data?.getParcelableExtra<LaunchParams>(HistoryActivity.RESULT)
      if (result != null) {
        launchParams = result
      }
      showLaunchParams()
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.launch_param, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.action_history -> {
        val intent = HistoryActivity.getLaunchIntent(this)
        startActivityForResult(intent, HistoryActivity.REQUEST_CODE)
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun onValueSet(type: Int, value: String) {
    when (type) {
      R.string.launch_param_package_name -> launchParams.packageName = value
      R.string.launch_param_class_name -> launchParams.className = value
      R.string.launch_param_data -> launchParams.data = value
    }
    showLaunchParams()
  }

  override fun onItemSelected(type: Int, position: Int) {
    when (type) {
      R.string.launch_param_action -> launchParams.action = position
      R.string.launch_param_mime_type -> launchParams.mimeType = position
    }
    showLaunchParams()
  }

  override fun onItemsSelected(type: Int, positions: ArrayList<Int>) {
    when (type) {
      R.string.launch_param_categories -> launchParams.categories = positions
      R.string.launch_param_flags -> launchParams.flags = positions
    }
    showLaunchParams()
  }

  override fun onValueSet(extra: LaunchParamsExtra, position: Int) {
    val extras = launchParams.extras
    if (position == -1) {
      extras.add(extra)
    } else {
      extras[position] = extra
    }
    showLaunchParams()
  }

  private fun configureRecyclerView(recyclerView: RecyclerView) {
    recyclerView.isNestedScrollingEnabled = false
    recyclerView.setHasFixedSize(true)
  }

  private fun bindInputValueDialog(viewId: Int, type: Int) {
    findViewById<View>(viewId).setOnClickListener {
      val initialValue = getValueInitialPosition(type)
      val dialog = ValueInputDialog.newInstance(type, initialValue)
      dialog.show(supportFragmentManager, ValueInputDialog.TAG)
    }
  }

  private fun bindSingleSelectionDialog(viewId: Int, type: Int, source: SelectionDialogSource) {
    findViewById<View>(viewId).setOnClickListener {
      val initialPosition = getSingleSelectionInitialPosition(type)
      val dialog = SingleSelectionDialog.newInstance(type, source, initialPosition)
      dialog.show(supportFragmentManager, SingleSelectionDialog.TAG)
    }
  }

  private fun bindMultiSelectionDialog(viewId: Int, type: Int, source: SelectionDialogSource) {
    findViewById<View>(viewId).setOnClickListener {
      val initialPositions = getMultiSelectionInitialPositions(type)
      val dialog = MultiSelectionDialog.newInstance(type, source, initialPositions)
      dialog.show(supportFragmentManager, MultiSelectionDialog.TAG)
    }
  }

  private fun bindKeyValueDialog(viewId: Int) {
    findViewById<View>(viewId).setOnClickListener {
      val size = launchParams.extras.size
      if (size >= EXTRAS_LIMIT && !appPreferences!!.isProVersion) {
        val dialog = GetPremiumDialog.newInstance(R.string.pro_version_unlock_extras)
        dialog.show(supportFragmentManager, GetPremiumDialog.TAG)
      } else {
        val dialog = ExtraInputDialog.newInstance(null, -1)
        dialog.show(supportFragmentManager, ExtraInputDialog.TAG)
      }
    }
  }

  private fun getValueInitialPosition(type: Int): String? {
    return when (type) {
      R.string.launch_param_package_name -> launchParams.packageName
      R.string.launch_param_class_name -> launchParams.className
      R.string.launch_param_data -> launchParams.data
      else -> throw IllegalStateException("Unknown type $type")
    }
  }

  private fun getSingleSelectionInitialPosition(type: Int): Int {
    return when (type) {
      R.string.launch_param_action -> launchParams.action
      R.string.launch_param_mime_type -> launchParams.mimeType
      else -> throw IllegalStateException("Unknown type $type")
    }
  }

  private fun getMultiSelectionInitialPositions(type: Int): ArrayList<Int> {
    return when (type) {
      R.string.launch_param_categories -> launchParams.categories
      R.string.launch_param_flags -> launchParams.flags
      else -> throw IllegalStateException("Unknown type $type")
    }
  }

  private fun showLaunchParams() {
    val packageName = launchParams.packageName
    packageNameView.text = packageName
    updateIcon(packageNameImageView, packageName)
    val className = launchParams.className
    classNameView.text = className
    updateIcon(classNameImageView, className)
    val data = launchParams.data
    dataView.text = data
    updateIcon(dataImageView, data)
    val actionValue = launchParams.actionValue
    actionView.text = actionValue
    updateIcon(actionImageView, actionValue)
    val mimeTypeValue = launchParams.mimeTypeValue
    mimeTypeView.text = mimeTypeValue
    updateIcon(mimeTypeImageView, mimeTypeValue)
    val extras = launchParams.extras
    extraAdapter!!.setItems(extras)
    updateIcon(extrasImageView, extras)
    updateExtrasAdd()
    val categoriesValues = launchParams.categoriesValues
    categoriesAdapter!!.setItems(categoriesValues)
    updateIcon(categoriesImageView, categoriesValues)
    val flagsValues = launchParams.flagsValues
    flagsAdapter!!.setItems(flagsValues)
    updateIcon(flagsImageView, flagsValues)
  }

  private fun updateExtrasAdd() {
    val extras = launchParams.extras
    if (extras.isEmpty()) {
      addExtraView.visibility = View.GONE
    } else {
      addExtraView.visibility = View.VISIBLE
    }
  }

  private fun updateIcon(imageView: ImageView, text: String?) {
    imageView.setImageResource(if (text.isNullOrBlank())
      R.drawable.ic_assignment
    else
      R.drawable.ic_assignment_done)
  }

  private fun updateIcon(imageView: ImageView, list: List<*>?) {
    imageView.setImageResource(if (list == null || list.isEmpty())
      R.drawable.ic_assignment
    else
      R.drawable.ic_assignment_done)
  }

  companion object {

    private const val ARG_ACTIVITY_MODEL = "arg_activity_model"
    private const val STATE_LAUNCH_PARAMS = "state_launch_params"

    private const val EXTRAS_LIMIT = 3

    fun start(context: Context, activityModel: ActivityModel?) {
      val starter = Intent(context, IntentBuilderActivity::class.java)
      starter.putExtra(ARG_ACTIVITY_MODEL, activityModel)
      context.startActivity(starter)
    }
  }
}
