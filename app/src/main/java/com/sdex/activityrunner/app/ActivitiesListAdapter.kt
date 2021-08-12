package com.sdex.activityrunner.app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.sdex.activityrunner.R
import com.sdex.activityrunner.app.dialog.ActivityOptionsDialog
import com.sdex.activityrunner.glide.GlideApp
import com.sdex.commons.BaseActivity
import kotlinx.android.synthetic.main.item_activity.view.*

class ActivitiesListAdapter(
    activity: BaseActivity
) : ListAdapter<ActivityModel, ActivitiesListAdapter.ViewHolder>(DIFF_CALLBACK) {

    private val glide = GlideApp.with(activity)
    private val launcher = ActivityLauncher(activity)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_activity, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), glide, launcher)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
            item: ActivityModel,
            glide: RequestManager,
            launcher: ActivityLauncher
        ) {
            itemView.name.text = item.name
            itemView.packageName.text = item.componentName.shortClassName

            glide.load(item)
                .apply(RequestOptions().fitCenter())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(itemView.icon)

            val context = itemView.context

            itemView.name.setTextColor(
                ContextCompat.getColor(
                    context, if (item.exported) {
                        R.color.text_color_primary
                    } else {
                        R.color.red
                    }
                )
            )

            itemView.setOnClickListener { launcher.launchActivity(item) }

            itemView.setOnLongClickListener {
                showActivityMenu(context, item)
                true
            }

            itemView.appMenu.setOnClickListener { showActivityMenu(context, item) }
        }

        private fun showActivityMenu(context: Context, model: ActivityModel) {
            val activity = context as FragmentActivity
            val dialog = ActivityOptionsDialog.newInstance(model)
            dialog.show(activity.supportFragmentManager, ActivityOptionsDialog.TAG)
        }
    }

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ActivityModel>() {

            override fun areItemsTheSame(
                oldItem: ActivityModel,
                newItem: ActivityModel
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ActivityModel,
                newItem: ActivityModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
