package com.sdex.activityrunner.app

import android.content.Context
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.sdex.activityrunner.R
import com.sdex.activityrunner.db.activity.ActivityModel
import com.sdex.activityrunner.glide.GlideApp
import kotlinx.android.synthetic.main.item_activity.view.*

class ActivitiesListAdapter(context: Context, private val callback: Callback) :
  ListAdapter<ActivityModel, ActivitiesListAdapter.ViewHolder>(DIFF_CALLBACK) {

  private val glide: RequestManager

  init {
    this.glide = GlideApp.with(context)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val view = inflater.inflate(R.layout.item_activity, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bindTo(getItem(position), glide, callback)
  }

  interface Callback {
    fun showShortcutDialog(item: ActivityModel)

    fun launchActivity(item: ActivityModel)

    fun launchActivityWithParams(item: ActivityModel)
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindTo(item: ActivityModel, glide: RequestManager, callback: Callback) {
      itemView.name.text = item.name
      itemView.packageName.text = item.componentName.shortClassName

      glide.load(item)
        .apply(RequestOptions().fitCenter())
        .into(itemView.icon)

      val context = itemView.context

      @ColorRes val color: Int = if (item.exported) {
        android.R.color.black
      } else {
        R.color.red
      }
      itemView.name.setTextColor(ContextCompat.getColor(context, color))

      itemView.setOnClickListener { callback.launchActivity(item) }

      itemView.appMenu.setOnClickListener { v ->
        val popup = PopupMenu(context, v)
        popup.inflate(R.menu.activity_item_menu)
        val menu = popup.menu
        menu.setGroupVisible(R.id.menu_group_activity_exported, item.exported)
        menu.setGroupVisible(R.id.menu_group_activity_not_exported, !item.exported)
        popup.show()
        popup.setOnMenuItemClickListener { menuItem ->
          when (menuItem.itemId) {
            R.id.action_activity_add_shortcut -> {
              callback.showShortcutDialog(item)
              return@setOnMenuItemClickListener true
            }
            R.id.action_activity_launch_with_params -> {
              callback.launchActivityWithParams(item)
              return@setOnMenuItemClickListener true
            }
            R.id.action_activity_launch_with_root -> {
              callback.launchActivity(item)
              return@setOnMenuItemClickListener true
            }
          }
          false
        }
      }

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
