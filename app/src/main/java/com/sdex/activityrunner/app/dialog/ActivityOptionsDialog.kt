package com.sdex.activityrunner.app.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sdex.activityrunner.app.ActivityLauncher
import com.sdex.activityrunner.app.ActivityModel
import com.sdex.activityrunner.databinding.DialogActivityMenuBinding
import com.sdex.activityrunner.shortcut.AddShortcutDialogActivity

class ActivityOptionsDialog : BottomSheetDialogFragment() {

    private var _binding: DialogActivityMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogActivityMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val model = requireArguments().getSerializable(ARG_MODEL) as ActivityModel

        val launcher = ActivityLauncher(requireActivity())

        binding.activityName.text = model.name
        binding.actionActivityAddShortcut.setOnClickListener {
            AddShortcutDialogActivity.start(requireContext(), model)
            dismissAllowingStateLoss()
        }
//        binding.action_activity_launch_with_params.visibility =
//            if (model.exported) View.VISIBLE else View.GONE
//        binding.action_activity_launch_with_params.setOnClickListener {
//            launcher.launchActivityWithParams(model)
//            dismissAllowingStateLoss()
//        }
        binding.actionActivityLaunchWithRoot.setOnClickListener {
            launcher.launchActivityWithRoot(model)
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

        fun newInstance(model: ActivityModel): ActivityOptionsDialog {
            val dialog = ActivityOptionsDialog()
            dialog.arguments = Bundle(1).apply {
                putSerializable(ARG_MODEL, model)
            }
            return dialog
        }
    }
}
