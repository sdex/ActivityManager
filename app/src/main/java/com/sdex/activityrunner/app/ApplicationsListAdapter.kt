package com.sdex.activityrunner.app

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.sdex.activityrunner.R
import com.sdex.activityrunner.app.dialog.ApplicationMenuDialog
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.glide.GlideApp
import com.sdex.activityrunner.preferences.AppPreferences
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import kotlinx.android.synthetic.main.item_application.view.*

class ApplicationsListAdapter(activity: androidx.fragment.app.FragmentActivity) : ListAdapter<ApplicationModel,
  ApplicationsListAdapter.AppViewHolder>(DIFF_CALLBACK),
  FastScrollRecyclerView.SectionedAdapter {

  private val glide: RequestManager
  private val appPreferences: AppPreferences

  init {
    this.glide = GlideApp.with(activity)
    this.appPreferences = AppPreferences(activity)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val view = inflater.inflate(R.layout.item_application, parent, false)
    return AppViewHolder(view)
  }

  override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
    holder.bindTo(getItem(position), glide, appPreferences)
  }

  override fun getSectionName(position: Int): String {
    val name = getItem(position).name
    return if (name.isNullOrEmpty()) "" else name.first().toUpperCase().toString()
  }

  class AppViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    fun bindTo(item: ApplicationModel, glide: RequestManager,
               appPreferences: AppPreferences) {
      itemView.name.text = item.name
      itemView.packageName.text = item.packageName

      val context = itemView.context

      itemView.system.visibility =
        if (item.system && appPreferences.isShowSystemAppIndicator) {
          VISIBLE
        } else {
          INVISIBLE
        }

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

    private fun showApplicationMenu(context: Context, model: ApplicationModel) {
      val activity = context as androidx.fragment.app.FragmentActivity
      val dialog = ApplicationMenuDialog.newInstance(model)
      dialog.show(activity.supportFragmentManager, ApplicationMenuDialog.TAG)
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
