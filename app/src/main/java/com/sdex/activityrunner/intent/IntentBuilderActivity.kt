package com.sdex.activityrunner.intent

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.sdex.activityrunner.R
import com.sdex.activityrunner.app.ActivityModel
import com.sdex.activityrunner.commons.BaseActivity
import com.sdex.activityrunner.databinding.ActivityIntentBuilderBinding
import com.sdex.activityrunner.extensions.parcelable
import com.sdex.activityrunner.extensions.serializable
import com.sdex.activityrunner.intent.LaunchParamsExtraListAdapter.Callback
import com.sdex.activityrunner.intent.converter.LaunchParamsToIntentConverter
import com.sdex.activityrunner.intent.dialog.ExtraInputDialog
import com.sdex.activityrunner.intent.dialog.MultiSelectionDialog
import com.sdex.activityrunner.intent.dialog.SingleSelectionDialog
import com.sdex.activityrunner.intent.dialog.ValueInputDialog
import com.sdex.activityrunner.intent.history.HistoryActivity
import com.sdex.activityrunner.intent.param.Action
import com.sdex.activityrunner.intent.param.MimeType
import com.sdex.activityrunner.util.IntentUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntentBuilderActivity : BaseActivity(),
    ValueInputDialog.OnValueInputDialogCallback, SingleSelectionDialog.OnItemSelectedCallback,
    MultiSelectionDialog.OnItemsSelectedCallback, ExtraInputDialog.OnKeyValueInputDialogCallback {

    private val viewModel: LaunchParamsViewModel by viewModels()
    private lateinit var binding: ActivityIntentBuilderBinding

    private val launchParams = LaunchParams()

    private val categoriesAdapter = LaunchParamsListAdapter()
    private val flagsAdapter = LaunchParamsListAdapter()
    private val extraAdapter = LaunchParamsExtraListAdapter()

    private val pickHistoryItem =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { intent ->
                val result = intent.parcelable<LaunchParams>(HistoryActivity.RESULT)
                launchParams.setFrom(result)
                showLaunchParams()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntentBuilderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(isBackButtonEnabled = true)

        val params = savedInstanceState?.parcelable<LaunchParams>(STATE_LAUNCH_PARAMS)
        launchParams.setFrom(params)

        val activityModel = intent.serializable<ActivityModel>(ARG_ACTIVITY_MODEL)

        title = activityModel?.name ?: getString(R.string.intent_launcher_activity)

        launchParams.packageName = activityModel?.packageName
        launchParams.className = activityModel?.className

        binding.listExtrasView.configureRecyclerView()
        binding.listCategoriesView.configureRecyclerView()
        binding.listFlagsView.configureRecyclerView()

        extraAdapter.callback = object : Callback {
            override fun onItemSelected(position: Int) {
                val extra = launchParams.extras[position]
                val dialog = ExtraInputDialog.newInstance(extra, position)
                dialog.show(supportFragmentManager, ExtraInputDialog.TAG)
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun removeItem(position: Int) {
                launchParams.extras.removeAt(position)
                extraAdapter.notifyDataSetChanged()
                binding.listExtrasView.requestLayout()
                updateExtrasAdd()
            }
        }
        extraAdapter.setHasStableIds(true)
        binding.listExtrasView.adapter = extraAdapter

        categoriesAdapter.setHasStableIds(true)
        binding.listCategoriesView.adapter = categoriesAdapter

        flagsAdapter.setHasStableIds(true)
        binding.listFlagsView.adapter = flagsAdapter

        bindInputValueDialog(binding.containerPackageName, R.string.launch_param_package_name)
        bindInputValueDialog(binding.containerClassName, R.string.launch_param_class_name)
        bindInputValueDialog(binding.containerData, R.string.launch_param_data)
        bindInputValueDialog(binding.actionEditImageView, R.string.launch_param_action)
        bindInputValueDialog(binding.mimeTypeEditImageView, R.string.launch_param_mime_type)
        bindSingleSelectionDialog(binding.containerAction, R.string.launch_param_action)
        bindSingleSelectionDialog(binding.containerMimeType, R.string.launch_param_mime_type)
        bindKeyValueDialog(binding.containerExtras)
        bindMultiSelectionDialog(
            binding.categoriesClickInterceptor,
            R.string.launch_param_categories
        )
        bindMultiSelectionDialog(binding.flagsClickInterceptor, R.string.launch_param_flags)

        binding.launch.setOnClickListener {
            if (binding.saveToHistory.isChecked) {
                viewModel.addToHistory(launchParams)
            }
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.launch_param, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_history -> {
                pickHistoryItem.launch(HistoryActivity.getLaunchIntent(this))
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

    private fun RecyclerView.configureRecyclerView() {
        isNestedScrollingEnabled = false
        setHasFixedSize(false)
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
            val dialog = ExtraInputDialog.newInstance(null, -1)
            dialog.show(supportFragmentManager, ExtraInputDialog.TAG)
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
        binding.packageNameView.text = launchParams.packageName
        binding.classNameView.text = launchParams.className
        binding.dataView.text = launchParams.data
        binding.actionView.text = launchParams.action
        binding.mimeTypeView.text = launchParams.mimeType
        extraAdapter.setItems(launchParams.extras)
        categoriesAdapter.setItems(launchParams.getCategoriesValues())
        flagsAdapter.setItems(launchParams.getFlagsValues())
        updateExtrasAdd()
    }

    private fun updateExtrasAdd() {
        val extras = launchParams.extras
        binding.addExtraView.isVisible = extras.isNotEmpty()
    }

    companion object {

        private const val ARG_ACTIVITY_MODEL = "arg_activity_model"
        private const val STATE_LAUNCH_PARAMS = "state_launch_params"

        fun start(context: Context, model: ActivityModel?) {
            val starter = Intent(context, IntentBuilderActivity::class.java)
            starter.putExtra(ARG_ACTIVITY_MODEL, model)
            context.startActivity(starter)
        }
    }
}
