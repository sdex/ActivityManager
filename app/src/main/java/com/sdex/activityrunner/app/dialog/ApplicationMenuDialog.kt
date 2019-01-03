package com.sdex.activityrunner.app.dialog

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sdex.activityrunner.R
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.glide.GlideApp
import com.sdex.activityrunner.manifest.ManifestViewerActivity
import com.sdex.activityrunner.util.IntentUtils
import com.sdex.commons.util.AppUtils
import kotlinx.android.synthetic.main.dialog_application_menu.*

class ApplicationMenuDialog : BottomSheetDialogFragment() {

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.dialog_application_menu, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val model = arguments?.getSerializable(ARG_MODEL) as ApplicationModel
    val packageName = model.packageName

    GlideApp.with(this)
      .load(model)
      .apply(RequestOptions().fitCenter())
      .transition(DrawableTransitionOptions.withCrossFade())
      .into(applicationIcon)

    applicationName.text = model.name
    val intent = activity!!.packageManager.getLaunchIntentForPackage(packageName)
    if (intent == null) {
      action_open_app.visibility = View.GONE
    }
    action_open_app.setOnClickListener {
      IntentUtils.launchApplication(activity!!, packageName)
      dismiss()
    }
    action_open_app_manifest.setOnClickListener {
      ManifestViewerActivity.start(activity!!, model)
      dismiss()
    }
    action_open_app_info.setOnClickListener {
      IntentUtils.openApplicationInfo(activity!!, packageName)
      dismiss()
    }
    action_open_app_play_store.setOnClickListener {
      AppUtils.openPlayStore(context, packageName)
      dismiss()
    }
  }

  override fun getTheme(): Int {
    val typedValue = TypedValue()
    activity!!.theme.resolveAttribute(R.attr.bottomDialogStyle, typedValue, true)
    return typedValue.data
  }

  companion object {

    const val TAG = "ApplicationMenuDialog"

    private const val ARG_MODEL = "arg_model"

    fun newInstance(model: ApplicationModel): ApplicationMenuDialog {
      val dialog = ApplicationMenuDialog()
      val args = Bundle(1)
      args.putSerializable(ARG_MODEL, model)
      dialog.arguments = args
      return dialog
    }
  }
}
