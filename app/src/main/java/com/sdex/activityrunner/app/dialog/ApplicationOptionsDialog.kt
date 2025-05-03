package com.sdex.activityrunner.app.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sdex.activityrunner.R
import com.sdex.activityrunner.databinding.DialogApplicationMenuBinding
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.extensions.createBottomSheetDialog
import com.sdex.activityrunner.extensions.serializable
import com.sdex.activityrunner.manifest.ManifestViewerActivity
import com.sdex.activityrunner.util.AppUtils
import com.sdex.activityrunner.util.IntentUtils

class ApplicationOptionsDialog : BottomSheetDialogFragment() {

    private var _binding: DialogApplicationMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return createBottomSheetDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val contextThemeWrapper = ContextThemeWrapper(activity, R.style.AppTheme)
        _binding = DialogApplicationMenuBinding.inflate(
            inflater.cloneInContext(contextThemeWrapper),
            container,
            false,
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val model = requireArguments().serializable<ApplicationModel>(ARG_MODEL)!!
        val packageName = model.packageName

        Glide.with(this)
            .load(model)
            .apply(RequestOptions().fitCenter())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.applicationIcon)

        binding.applicationName.text = model.name
        binding.packageName.text = model.packageName
        binding.version.text = getString(
            R.string.app_version_format,
            model.versionName,
            model.versionCode,
        )

        val totalActivitiesFormattedText = resources.getQuantityString(
            R.plurals.activities_count,
            model.activitiesCount,
            model.activitiesCount,
        )
        binding.activities.text = getString(
            R.string.app_info_activities_number,
            totalActivitiesFormattedText,
            model.exportedActivitiesCount,
        )

        val intent = requireActivity().packageManager.getLaunchIntentForPackage(packageName)
        binding.actionOpenApp.isVisible = intent != null
        binding.actionOpenApp.setOnClickListener {
            IntentUtils.launchApplication(requireActivity(), packageName)
            dismissAllowingStateLoss()
        }
        binding.actionOpenAppManifest.setOnClickListener {
            ManifestViewerActivity.start(requireActivity(), model)
            dismissAllowingStateLoss()
        }
        binding.actionOpenAppInfo.setOnClickListener {
            IntentUtils.openApplicationInfo(requireActivity(), packageName)
            dismissAllowingStateLoss()
        }
        binding.actionOpenAppPlayStore.setOnClickListener {
            AppUtils.openPlayStore(context, packageName)
            dismissAllowingStateLoss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        const val TAG = "ApplicationMenuDialog"

        private const val ARG_MODEL = "arg_model"

        fun newInstance(model: ApplicationModel) = ApplicationOptionsDialog().apply {
            arguments = bundleOf(ARG_MODEL to model)
        }
    }
}
