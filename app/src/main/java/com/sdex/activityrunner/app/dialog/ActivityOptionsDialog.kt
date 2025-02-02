package com.sdex.activityrunner.app.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sdex.activityrunner.R
import com.sdex.activityrunner.app.ActivityModel
import com.sdex.activityrunner.app.launchActivity
import com.sdex.activityrunner.databinding.DialogActivityMenuBinding
import com.sdex.activityrunner.extensions.createBottomSheetDialog
import com.sdex.activityrunner.extensions.serializable
import com.sdex.activityrunner.shortcut.AddShortcutDialogActivity

class ActivityOptionsDialog : BottomSheetDialogFragment() {

    private var _binding: DialogActivityMenuBinding? = null
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
        _binding = DialogActivityMenuBinding.inflate(
            inflater.cloneInContext(contextThemeWrapper),
            container,
            false,
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val model = requireArguments().serializable<ActivityModel>(ARG_MODEL)!!

        binding.activityName.text = model.name
        binding.actionActivityAddShortcut.setOnClickListener {
            AddShortcutDialogActivity.start(requireContext(), model)
            dismissAllowingStateLoss()
        }
        binding.actionActivityLaunchWithParams.isVisible = model.exported
        binding.actionActivityLaunchWithParams.setOnClickListener {
            requireActivity().launchActivity(model, useParams = true)
            dismissAllowingStateLoss()
        }
        binding.actionActivityLaunchWithRoot.setOnClickListener {
            requireActivity().launchActivity(model, useRoot = true)
            dismissAllowingStateLoss()
        }
        binding.rootSettings.setOnClickListener {
            RootConfigDialog.newInstance()
                .show(parentFragmentManager, RootConfigDialog.TAG)
            dismissAllowingStateLoss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        const val TAG = "ActivityMenuDialog"

        private const val ARG_MODEL = "arg_model"

        fun newInstance(model: ActivityModel) = ActivityOptionsDialog().apply {
            arguments = bundleOf(ARG_MODEL to model)
        }
    }
}
