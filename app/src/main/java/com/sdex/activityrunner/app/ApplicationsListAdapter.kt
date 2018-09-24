package com.sdex.activityrunner.app

import android.content.Context
import android.preference.PreferenceManager
import android.support.v4.app.FragmentActivity
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
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
import com.sdex.activityrunner.preferences.AdvancedPreferences
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import kotlinx.android.synthetic.main.item_application.view.*

class ApplicationsListAdapter(activity: FragmentActivity) : ListAdapter<ApplicationModel,
  ApplicationsListAdapter.AppViewHolder>(DIFF_CALLBACK),
  FastScrollRecyclerView.SectionedAdapter {

  private val glide: RequestManager
  private val advancedPreferences: AdvancedPreferences

  init {
    this.glide = GlideApp.with(activity)
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
    this.advancedPreferences = AdvancedPreferences(sharedPreferences)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val view = inflater.inflate(R.layout.item_application, parent, false)
    return AppViewHolder(view)
  }

  override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
    holder.bindTo(getItem(position), glide, advancedPreferences)
  }

  override fun getSectionName(position: Int): String {
    val name = getItem(position).name
    return if (name.isNullOrEmpty()) "" else name!!.first().toUpperCase().toString()
  }

  class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindTo(item: ApplicationModel, glide: RequestManager,
               advancedPreferences: AdvancedPreferences) {
      itemView.name.text = item.name
      itemView.packageName.text = item.packageName

      val context = itemView.context

      itemView.system.visibility =
        if (item.system && advancedPreferences.isShowSystemAppIndicator) {
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
      val activity = context as FragmentActivity
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
