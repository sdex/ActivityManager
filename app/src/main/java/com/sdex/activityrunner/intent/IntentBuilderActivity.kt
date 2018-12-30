package com.sdex.activityrunner.intent

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.ViewModelProviders
import com.sdex.activityrunner.R
import com.sdex.activityrunner.app.ActivityModel
import com.sdex.activityrunner.extensions.enableBackButton
import com.sdex.activityrunner.intent.LaunchParamsExtraListAdapter.Callback
import com.sdex.activityrunner.intent.converter.LaunchParamsToIntentConverter
import com.sdex.activityrunner.intent.dialog.ExtraInputDialog
import com.sdex.activityrunner.intent.dialog.MultiSelectionDialog
import com.sdex.activityrunner.intent.dialog.SingleSelectionDialog
import com.sdex.activityrunner.intent.dialog.ValueInputDialog
import com.sdex.activityrunner.intent.history.HistoryActivity
import com.sdex.activityrunner.intent.param.Action
import com.sdex.activityrunner.intent.param.MimeType
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.premium.GetPremiumDialog
import com.sdex.activityrunner.util.IntentUtils
import com.sdex.commons.BaseActivity
import kotlinx.android.synthetic.main.activity_intent_builder.*
import java.util.*
import kotlin.properties.Delegates

class IntentBuilderActivity : BaseActivity(),
  ValueInputDialog.OnValueInputDialogCallback, SingleSelectionDialog.OnItemSelectedCallback,
  MultiSelectionDialog.OnItemsSelectedCallback, ExtraInputDialog.OnKeyValueInputDialogCallback {

  private val launchParams: LaunchParams = LaunchParams()

  private val categoriesAdapter = LaunchParamsListAdapter()
  private val flagsAdapter = LaunchParamsListAdapter()
  private val extraAdapter = LaunchParamsExtraListAdapter()

  private val viewModel: LaunchParamsViewModel by lazy {
    ViewModelProviders.of(this).get(LaunchParamsViewModel::class.java)
  }

  private var appPreferences: AppPreferences by Delegates.notNull()

  override fun getLayout(): Int {
    return R.layout.activity_intent_builder
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val params = savedInstanceState?.getParcelable(STATE_LAUNCH_PARAMS) as LaunchParams?
    launchParams.setFrom(params)

    appPreferences = AppPreferences(this)

    enableBackButton()
    val activityModel = intent.getSerializableExtra(ARG_ACTIVITY_MODEL) as ActivityModel?

    title = activityModel?.name ?: getString(R.string.intent_launcher_activity)

    launchParams.packageName = activityModel?.packageName
    launchParams.className = activityModel?.className

    configureRecyclerView(listExtrasView)
    configureRecyclerView(listCategoriesView)
    configureRecyclerView(listFlagsView)

    extraAdapter.callback = object : Callback {
      override fun onItemSelected(position: Int) {
        val extra = launchParams.extras[position]
        val dialog = ExtraInputDialog.newInstance(extra, position)
        dialog.show(supportFragmentManager, ExtraInputDialog.TAG)
      }

      override fun removeItem(position: Int) {
        launchParams.extras.removeAt(position)
        extraAdapter.notifyDataSetChanged()
        listExtrasView.requestLayout()
        updateExtrasAdd()
      }
    }
    extraAdapter.setHasStableIds(true)
    listExtrasView.adapter = extraAdapter

    categoriesAdapter.setHasStableIds(true)
    listCategoriesView.adapter = categoriesAdapter

    flagsAdapter.setHasStableIds(true)
    listFlagsView.adapter = flagsAdapter

    bindInputValueDialog(container_package_name, R.string.launch_param_package_name)
    bindInputValueDialog(container_class_name, R.string.launch_param_class_name)
    bindInputValueDialog(container_data, R.string.launch_param_data)
    bindInputValueDialog(actionEditImageView, R.string.launch_param_action)
    bindInputValueDialog(mimeTypeEditImageView, R.string.launch_param_mime_type)
    bindSingleSelectionDialog(container_action, R.string.launch_param_action)
    bindSingleSelectionDialog(container_mime_type, R.string.launch_param_mime_type)
    bindKeyValueDialog(container_extras)
    bindMultiSelectionDialog(categories_click_interceptor, R.string.launch_param_categories)
    bindMultiSelectionDialog(flags_click_interceptor, R.string.launch_param_flags)

    launch.setOnClickListener {
      viewModel.addToHistory(launchParams)
      val converter = LaunchParamsToIntentConverter(launchParams)
      val intent = converter.convert()
      IntentUtils.launchActivity(this@IntentBuilderActivity, intent)
    }

    showLaunchParams()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putParcelable(STATE_LAUNCH_PARAMS, launchParams)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == HistoryActivity.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
      val result = data?.getParcelableExtra<LaunchParams>(HistoryActivity.RESULT)
      launchParams.setFrom(result)
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
      R.string.launch_param_action -> launchParams.action = value
      R.string.launch_param_mime_type -> launchParams.mimeType = value
    }
    showLaunchParams()
  }

  override fun onItemSelected(type: Int, position: Int) {
    when (type) {
      R.string.launch_param_action -> {
        launchParams.action = if (position == 0) null
        else Action.getAction(Action.list()[position])
      }
      R.string.launch_param_mime_type -> {
        launchParams.mimeType = if (position == 0) null
        else MimeType.list()[position]
      }
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

  private fun configureRecyclerView(recyclerView: androidx.recyclerview.widget.RecyclerView) {
    recyclerView.isNestedScrollingEnabled = false
    recyclerView.setHasFixedSize(false)
  }

  private fun bindInputValueDialog(view: View, type: Int) {
    view.setOnClickListener {
      val initialValue = getValueInitialPosition(type)
      val dialog = ValueInputDialog.newInstance(type, initialValue)
      dialog.show(supportFragmentManager, ValueInputDialog.TAG)
    }
  }

  private fun bindSingleSelectionDialog(view: View, type: Int) {
    view.setOnClickListener {
      val initialPosition = getSingleSelectionInitialPosition(type)
      val dialog = SingleSelectionDialog.newInstance(type, initialPosition)
      dialog.show(supportFragmentManager, SingleSelectionDialog.TAG)
    }
  }

  private fun bindMultiSelectionDialog(view: View, type: Int) {
    view.setOnClickListener {
      val initialPositions = getMultiSelectionInitialPositions(type)
      val dialog = MultiSelectionDialog.newInstance(type, initialPositions)
      dialog.show(supportFragmentManager, MultiSelectionDialog.TAG)
    }
  }

  private fun bindKeyValueDialog(view: View) {
    view.setOnClickListener {
      val size = launchParams.extras.size
      if (size >= EXTRAS_LIMIT && !appPreferences.isProVersion) {
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
      R.string.launch_param_action -> launchParams.action
      R.string.launch_param_mime_type -> launchParams.mimeType
      else -> throw IllegalStateException("Unknown type $type")
    }
  }

  private fun getSingleSelectionInitialPosition(type: Int): Int {
    return when (type) {
      R.string.launch_param_action -> {
        if (launchParams.action == null) 0
        else Action.getActionKeyPosition(launchParams.action!!)
      }
      R.string.launch_param_mime_type -> {
        if (launchParams.mimeType == null) 0
        else MimeType.list().indexOf(launchParams.mimeType!!)
      }
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
    val actionValue = launchParams.action
    actionView.text = actionValue
    updateIcon(actionImageView, actionValue)
    val mimeTypeValue = launchParams.mimeType
    mimeTypeView.text = mimeTypeValue
    updateIcon(mimeTypeImageView, mimeTypeValue)
    val extras = launchParams.extras
    extraAdapter.setItems(extras)
    updateIcon(extrasImageView, extras)
    updateExtrasAdd()
    val categoriesValues = launchParams.getCategoriesValues()
    categoriesAdapter.setItems(categoriesValues)
    updateIcon(categoriesImageView, categoriesValues)
    val flagsValues = launchParams.getFlagsValues()
    flagsAdapter.setItems(flagsValues)
    updateIcon(flagsImageView, flagsValues)
  }

  private fun updateExtrasAdd() {
    val extras = launchParams.extras
    addExtraView.visibility = if (extras.isEmpty()) View.GONE else View.VISIBLE
  }

  private fun updateIcon(imageView: ImageView, text: String?) {
    imageView.isSelected = !text.isNullOrEmpty()
  }

  private fun updateIcon(imageView: ImageView, list: List<*>) {
    imageView.isSelected = list.isNotEmpty()
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
