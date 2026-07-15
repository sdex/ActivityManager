package com.sdex.activityrunner.intent

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sdex.activityrunner.R
import com.sdex.activityrunner.app.ActivityModel
import com.sdex.activityrunner.commons.BaseActivity
import com.sdex.activityrunner.databinding.ActivityIntentBuilderBinding
import com.sdex.activityrunner.extensions.parcelable
import com.sdex.activityrunner.extensions.serializable
import com.sdex.activityrunner.intent.LaunchParamsExtraListAdapter.Callback
import com.sdex.activityrunner.intent.converter.LaunchParamsToShellCommandConverter
import com.sdex.activityrunner.intent.dialog.ExtraInputDialog
import com.sdex.activityrunner.intent.dialog.MultiSelectionDialog
import com.sdex.activityrunner.intent.dialog.SingleSelectionDialog
import com.sdex.activityrunner.intent.dialog.ValueInputDialog
import com.sdex.activityrunner.intent.history.HistoryActivity
import com.sdex.activityrunner.preferences.AppPreferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class IntentBuilderActivity : BaseActivity() {

    @Inject
    lateinit var appPreferences: AppPreferences

    private val viewModel: LaunchParamsViewModel by viewModels()
    private lateinit var binding: ActivityIntentBuilderBinding

    private val categoriesAdapter = LaunchParamsListAdapter()
    private val flagsAdapter = LaunchParamsListAdapter()
    private val extraAdapter = LaunchParamsExtraListAdapter()

    private val pickHistoryItem =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { intent ->
                val result = intent.parcelable<LaunchParams>(HistoryActivity.RESULT)
                viewModel.setLaunchParams(result)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntentBuilderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(isBackButtonEnabled = true)

        val activityModel = intent.serializable<ActivityModel>(ARG_ACTIVITY_MODEL)
        viewModel.initialize(activityModel)

        title = activityModel?.name ?: getString(R.string.intent_launcher_activity)

        binding.listExtrasView.configureRecyclerView()
        binding.listCategoriesView.configureRecyclerView()
        binding.listFlagsView.configureRecyclerView()

        extraAdapter.callback = object : Callback {
            override fun onItemSelected(position: Int) {
                val extra = viewModel.launchParamsState.value.extras[position]
                val dialog = ExtraInputDialog.newInstance(extra, position)
                dialog.show(supportFragmentManager, ExtraInputDialog.TAG)
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun removeItem(position: Int) {
                viewModel.removeExtra(position)
                extraAdapter.notifyDataSetChanged()
                binding.listExtrasView.requestLayout()
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
            R.string.launch_param_categories,
        )
        bindMultiSelectionDialog(binding.flagsClickInterceptor, R.string.launch_param_flags)

        binding.launchWithRoot.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setUseRoot(isChecked)
        }

        binding.launchWithRoot.isChecked = (activityModel?.launchRequiresRoot == true)

        binding.launch.setOnClickListener {
            viewModel.launch(binding.saveToHistory.isChecked)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.launchParamsState.collect {
                        showLaunchParams(it)
                    }
                }
                launch {
                    viewModel.events.collect {
                        handleLaunchEvent(it)
                    }
                }
            }
        }
    }

    private fun handleLaunchEvent(event: LaunchParamsViewModel.LaunchEvent) {
        when (event) {
            is LaunchParamsViewModel.LaunchEvent.LaunchError ->
                showErrorDialog(R.string.starting_activity_intent_failed, event.details)

            LaunchParamsViewModel.LaunchEvent.RootUnavailable ->
                Toast.makeText(this, R.string.starting_activity_root_not_available, Toast.LENGTH_SHORT)
                    .show()

            LaunchParamsViewModel.LaunchEvent.RootSuccess -> if (appPreferences.isShowLaunchToast) {
                Toast.makeText(this, R.string.starting_activity_intent, Toast.LENGTH_SHORT).show()
            }

            is LaunchParamsViewModel.LaunchEvent.RootError ->
                showErrorDialog(R.string.starting_activity_root_error, event.details)
        }
    }

    private fun showErrorDialog(@StringRes titleRes: Int, message: String?) {
        MaterialAlertDialogBuilder(this)
            .setTitle(titleRes)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .show().apply {
                val messageTextView = findViewById<TextView>(android.R.id.message)
                messageTextView?.setTextIsSelectable(true)
            }
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

    private fun RecyclerView.configureRecyclerView() {
        isNestedScrollingEnabled = false
        setHasFixedSize(false)
    }

    private fun bindInputValueDialog(view: View, type: Int) {
        view.setOnClickListener {
            val initialValue = viewModel.getValueInitialValue(type)
            val dialog = ValueInputDialog.newInstance(type, initialValue)
            dialog.show(supportFragmentManager, ValueInputDialog.TAG)
        }
    }

    private fun bindSingleSelectionDialog(view: View, type: Int) {
        view.setOnClickListener {
            val initialPosition = viewModel.getSingleSelectionInitialPosition(type)
            val dialog = SingleSelectionDialog.newInstance(type, initialPosition)
            dialog.show(supportFragmentManager, SingleSelectionDialog.TAG)
        }
    }

    private fun bindMultiSelectionDialog(view: View, type: Int) {
        view.setOnClickListener {
            val initialPositions = viewModel.getMultiSelectionInitialPositions(type)
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

    private fun showLaunchParams(launchParams: LaunchParams) {
        binding.packageNameView.text = launchParams.packageName
        binding.classNameView.text = launchParams.className
        binding.dataView.text = launchParams.data
        binding.actionView.text = launchParams.action
        binding.mimeTypeView.text = launchParams.mimeType
        extraAdapter.setItems(launchParams.extras)
        categoriesAdapter.setItems(launchParams.getCategoriesValues())
        flagsAdapter.setItems(launchParams.getFlagsValues())
        if (binding.launchWithRoot.isChecked != launchParams.useRoot) {
            binding.launchWithRoot.isChecked = launchParams.useRoot
        }
        binding.rootExtrasWarning.isVisible = launchParams.useRoot &&
            LaunchParamsToShellCommandConverter.getUnsupportedExtras(launchParams).isNotEmpty()
        updateExtrasAdd(launchParams)
    }

    private fun updateExtrasAdd(launchParams: LaunchParams) {
        binding.addExtraView.isVisible = launchParams.extras.isNotEmpty()
    }

    companion object {

        private const val ARG_ACTIVITY_MODEL = "arg_activity_model"

        fun start(context: Context, model: ActivityModel?) {
            val starter = Intent(context, IntentBuilderActivity::class.java)
            starter.putExtra(ARG_ACTIVITY_MODEL, model)
            context.startActivity(starter)
        }
    }
}
