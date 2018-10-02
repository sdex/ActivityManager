package com.sdex.activityrunner.app

import android.content.Context
import android.support.annotation.ColorRes
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.sdex.activityrunner.R
import com.sdex.activityrunner.app.dialog.ActivityMenuDialog
import com.sdex.activityrunner.glide.GlideApp
import com.sdex.activityrunner.ui.SnackbarContainerActivity
import kotlinx.android.synthetic.main.item_activity.view.*

class ActivitiesListAdapter(snackbarContainerActivity: SnackbarContainerActivity) :
  ListAdapter<ActivityModel, ActivitiesListAdapter.ViewHolder>(DIFF_CALLBACK) {

  private val glide: RequestManager
  private val launcher: ActivityLauncher

  init {
    this.glide = GlideApp.with(snackbarContainerActivity.getActivity())
    this.launcher = ActivityLauncher(snackbarContainerActivity)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val view = inflater.inflate(R.layout.item_activity, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bindTo(getItem(position), glide, launcher)
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindTo(item: ActivityModel, glide: RequestManager, launcher: ActivityLauncher) {
      itemView.name.text = item.name
      itemView.packageName.text = item.componentName.shortClassName

      glide.load(item)
        .apply(RequestOptions().fitCenter())
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(itemView.icon)

      val context = itemView.context

      @ColorRes val color: Int = if (item.exported) {
        android.R.color.black
      } else {
        R.color.red
      }
      itemView.name.setTextColor(ContextCompat.getColor(context, color))

      itemView.setOnClickListener { launcher.launchActivity(item) }

      itemView.setOnLongClickListener {
        showActivityMenu(context, item)
        true
      }

      itemView.appMenu.setOnClickListener { showActivityMenu(context, item) }
    }

    private fun showActivityMenu(context: Context, model: ActivityModel) {
      val activity = context as FragmentActivity
      val dialog = ActivityMenuDialog.newInstance(model)
      dialog.show(activity.supportFragmentManager, ActivityMenuDialog.TAG)
    }
  }

  companion object {

    val DIFF_CALLBACK: DiffUtil.ItemCallback<ActivityModel> = object : DiffUtil.ItemCallback<ActivityModel>() {

      override fun areItemsTheSame(oldItem: ActivityModel, newItem: ActivityModel): Boolean {
        return oldItem == newItem
      }

      override fun areContentsTheSame(oldItem: ActivityModel, newItem: ActivityModel): Boolean {
        return oldItem == newItem
      }
    }
  }
}
