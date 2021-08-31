package com.sdex.activityrunner.app.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sdex.activityrunner.databinding.DialogApplicationMenuBinding
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.glide.GlideApp
import com.sdex.activityrunner.manifest.ManifestViewerActivity
import com.sdex.activityrunner.util.IntentUtils
import com.sdex.commons.util.AppUtils

class ApplicationOptionsDialog : BottomSheetDialogFragment() {

    private var _binding: DialogApplicationMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogApplicationMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val model = requireArguments().getSerializable(ARG_MODEL) as ApplicationModel
        val packageName = model.packageName

        GlideApp.with(this)
            .load(model)
            .apply(RequestOptions().fitCenter())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.applicationIcon)

        binding.applicationName.text = model.name
        val intent = requireActivity().packageManager.getLaunchIntentForPackage(packageName)
        if (intent == null) {
            binding.actionOpenApp.visibility = View.GONE
        }
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

        fun newInstance(model: ApplicationModel): ApplicationOptionsDialog {
            val dialog = ApplicationOptionsDialog()
            dialog.arguments = Bundle(1).apply {
                putSerializable(ARG_MODEL, model)
            }
            return dialog
        }
    }
}
