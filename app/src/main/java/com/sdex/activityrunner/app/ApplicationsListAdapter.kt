package com.sdex.activityrunner.app

import android.content.Context
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.FragmentActivity
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.sdex.activityrunner.R
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.glide.GlideApp
import com.sdex.activityrunner.manifest.ManifestViewerActivity
import com.sdex.activityrunner.util.IntentUtils
import com.sdex.commons.util.AppUtils
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import kotlinx.android.synthetic.main.item_application.view.*

class ApplicationsListAdapter(activity: FragmentActivity) : ListAdapter<ApplicationModel,
  ApplicationsListAdapter.AppViewHolder>(DIFF_CALLBACK),
  FastScrollRecyclerView.SectionedAdapter {

  private val glide: RequestManager

  init {
    this.glide = GlideApp.with(activity)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val view = inflater.inflate(R.layout.item_application, parent, false)
    return AppViewHolder(view)
  }

  override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
    holder.bindTo(getItem(position), glide)
  }

  override fun getSectionName(position: Int): String {
    val name = getItem(position).name
    return if (name.isNullOrEmpty()) "" else name!!.first().toUpperCase().toString()
  }

  class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindTo(item: ApplicationModel, glide: RequestManager) {
      itemView.name.text = item.name
      itemView.packageName.text = item.packageName

      val context = itemView.context

      glide.load(item)
        .apply(RequestOptions().fitCenter())
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(itemView.icon)

      itemView.setOnClickListener { ActivitiesListActivity.start(context, item) }

      itemView.setOnLongClickListener {
        showApplicationMenu(context, item)
        true
      }

      itemView.appMenu.setOnClickListener { showApplicationMenu(context, item) }
    }

    private fun showApplicationMenu(context: Context, applicationModel: ApplicationModel) {
      val view = View.inflate(context, R.layout.dialog_application_menu, null)
      val dialog = BottomSheetDialog(context)
      dialog.setContentView(view)
      val packageName = applicationModel.packageName
      view.findViewById<TextView>(R.id.application_name).text = applicationModel.name
      view.findViewById<View>(R.id.action_open_app_manifest).setOnClickListener {
        ManifestViewerActivity.start(context, packageName, applicationModel.name)
        dialog.dismiss()
      }
      view.findViewById<View>(R.id.action_open_app_info).setOnClickListener {
        IntentUtils.openApplicationInfo(context, packageName)
        dialog.dismiss()
      }
      view.findViewById<View>(R.id.action_open_app_play_store).setOnClickListener {
        AppUtils.openPlayStore(context, packageName)
        dialog.dismiss()
      }
      dialog.show()
    }
  }

  companion object {

    val DIFF_CALLBACK: DiffUtil.ItemCallback<ApplicationModel> = object : DiffUtil.ItemCallback<ApplicationModel>() {

      override fun areItemsTheSame(oldItem: ApplicationModel, newItem: ApplicationModel): Boolean {
        return oldItem.packageName == newItem.packageName
      }

      override fun areContentsTheSame(oldItem: ApplicationModel, newItem: ApplicationModel): Boolean {
        return oldItem == newItem
      }
    }
  }
}
