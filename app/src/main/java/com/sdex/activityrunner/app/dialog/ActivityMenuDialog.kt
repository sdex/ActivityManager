package com.sdex.activityrunner.app.dialog

import android.content.Context
import android.os.Bundle

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sdex.activityrunner.R
import com.sdex.activityrunner.app.ActivityLauncher
import com.sdex.activityrunner.app.ActivityModel
import com.sdex.activityrunner.shortcut.AddShortcutDialogActivity
import com.sdex.activityrunner.ui.SnackbarContainerActivity
import kotlinx.android.synthetic.main.dialog_activity_menu.*

class ActivityMenuDialog : BottomSheetDialogFragment() {

    private var snackbarContainerActivity: SnackbarContainerActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_activity_menu, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SnackbarContainerActivity) {
            snackbarContainerActivity = context
        } else {
            throw IllegalArgumentException(
                "$context!!::class.java.simpleName not implement SnackbarContainerActivity"
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val model = arguments?.getSerializable(ARG_MODEL) as ActivityModel

        val launcher = ActivityLauncher(snackbarContainerActivity!!)

        activity_name.text = model.name
        action_activity_add_shortcut.setOnClickListener {
            showShortcutDialog(model)
            dismiss()
        }
//        action_activity_launch_with_params.visibility =
//            if (model.exported) View.VISIBLE else View.GONE
//        action_activity_launch_with_params.setOnClickListener {
//            launcher.launchActivityWithParams(model)
//            dismiss()
//        }
        action_activity_launch_with_root.setOnClickListener {
            launcher.launchActivityWithRoot(model)
            dismiss()
        }
    }

    override fun getTheme(): Int {
        val typedValue = TypedValue()
        requireActivity().theme.resolveAttribute(R.attr.bottomDialogStyle, typedValue, true)
        return typedValue.data
    }

    private fun showShortcutDialog(item: ActivityModel) {
        AddShortcutDialogActivity.start(requireContext(), item)
    }

    companion object {

        const val TAG = "ActivityMenuDialog"

        private const val ARG_MODEL = "arg_model"

        fun newInstance(model: ActivityModel): ActivityMenuDialog {
            val dialog = ActivityMenuDialog()
            val args = Bundle(1)
            args.putSerializable(ARG_MODEL, model)
            dialog.arguments = args
            return dialog
        }
    }
}
