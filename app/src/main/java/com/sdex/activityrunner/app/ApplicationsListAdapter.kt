package com.sdex.activityrunner.app

import android.content.Context
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.sdex.activityrunner.R
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.glide.GlideApp
import com.sdex.activityrunner.manifest.ManifestViewerActivity
import com.sdex.activityrunner.util.IntentUtils
import com.sdex.commons.util.AppUtils
import kotlinx.android.synthetic.main.item_application.view.*

class ApplicationsListAdapter(context: Context) : ListAdapter<ApplicationModel,
  ApplicationsListAdapter.AppViewHolder>(DIFF_CALLBACK) {

  private val glide: RequestManager

  init {
    this.glide = GlideApp.with(context)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val view = inflater.inflate(R.layout.item_application, parent, false)
    return AppViewHolder(view)
  }

  override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
    holder.bindTo(getItem(position), glide)
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

      itemView.appMenu.setOnClickListener { v ->
        val popup = PopupMenu(context, v)
        popup.inflate(R.menu.application_item_menu)
        popup.show()
        popup.setOnMenuItemClickListener { menuItem ->
          val packageName = item.packageName
          when (menuItem.itemId) {
            R.id.action_open_app -> {
              AppUtils.openPlayStore(context, packageName)
              return@setOnMenuItemClickListener true
            }
            R.id.action_open_app_info -> {
              IntentUtils.openApplicationInfo(context, packageName)
              return@setOnMenuItemClickListener true
            }
            R.id.action_open_app_manifest -> {
              ManifestViewerActivity.start(context, packageName, item.name)
              return@setOnMenuItemClickListener true
            }
          }
          false
        }
      }

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
