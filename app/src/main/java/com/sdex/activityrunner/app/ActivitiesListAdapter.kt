package com.sdex.activityrunner.app

import android.content.Context
import android.support.annotation.ColorRes
import android.support.design.widget.BottomSheetDialog
import android.support.v4.content.ContextCompat
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

    fun launchActivityWithRoot(item: ActivityModel)

    fun launchActivityWithParams(item: ActivityModel)
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindTo(item: ActivityModel, glide: RequestManager, callback: Callback) {
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

      itemView.setOnClickListener { callback.launchActivity(item) }

      itemView.setOnLongClickListener {
        showActivityMenu(context, item, callback)
        true
      }

      itemView.appMenu.setOnClickListener { v ->
        showActivityMenu(context, item, callback)
      }

    }

    private fun showActivityMenu(context: Context, activityModel: ActivityModel,
                                 callback: Callback) {
      val view = View.inflate(context, R.layout.dialog_activity_menu, null)
      val dialog = BottomSheetDialog(context)
      dialog.setContentView(view)
      view.findViewById<TextView>(R.id.activity_name).text = activityModel.name
      view.findViewById<View>(R.id.action_activity_add_shortcut).setOnClickListener {
        callback.showShortcutDialog(activityModel)
        dialog.dismiss()
      }
      val itemParams = view.findViewById<View>(R.id.action_activity_launch_with_params)
      itemParams.visibility = if (activityModel.exported) View.VISIBLE else View.GONE
      itemParams.setOnClickListener {
        callback.launchActivityWithParams(activityModel)
        dialog.dismiss()
      }
      view.findViewById<View>(R.id.action_activity_launch_with_root).setOnClickListener {
        callback.launchActivityWithRoot(activityModel)
        dialog.dismiss()
      }
      dialog.show()
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
