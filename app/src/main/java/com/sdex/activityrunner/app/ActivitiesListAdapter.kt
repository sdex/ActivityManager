package com.sdex.activityrunner.app

import android.content.Context
import androidx.annotation.ColorInt
import androidx.fragment.app.FragmentActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.util.TypedValue
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
  @ColorInt
  private val primaryTextColor: Int

  init {
    val activity = snackbarContainerActivity.getActivity()
    this.glide = GlideApp.with(activity)
    this.launcher = ActivityLauncher(snackbarContainerActivity)

    val typedValue = TypedValue()
    activity.theme.resolveAttribute(R.attr.primaryTextColor, typedValue, true)
    primaryTextColor = typedValue.data
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val view = inflater.inflate(R.layout.item_activity, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bindTo(getItem(position), primaryTextColor, glide, launcher)
  }

  class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    fun bindTo(item: ActivityModel, primaryTextColor: Int,
               glide: RequestManager, launcher: ActivityLauncher) {
      itemView.name.text = item.name
      itemView.packageName.text = item.componentName.shortClassName

      glide.load(item)
        .apply(RequestOptions().fitCenter())
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(itemView.icon)

      val context = itemView.context

      @ColorInt val color: Int = if (item.exported) {
        primaryTextColor
      } else {
        ContextCompat.getColor(context, R.color.red)
      }
      itemView.name.setTextColor(color)

      itemView.setOnClickListener { launcher.launchActivity(item) }

      itemView.setOnLongClickListener {
        showActivityMenu(context, item)
        true
      }

      itemView.appMenu.setOnClickListener { showActivityMenu(context, item) }
    }

    private fun showActivityMenu(context: Context, model: ActivityModel) {
      val activity = context as androidx.fragment.app.FragmentActivity
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
