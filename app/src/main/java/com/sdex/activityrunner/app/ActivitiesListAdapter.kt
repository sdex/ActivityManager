package com.sdex.activityrunner.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.sdex.activityrunner.R
import com.sdex.activityrunner.databinding.ItemActivityBinding
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.extensions.resolveColorAttr
import com.sdex.activityrunner.glide.GlideApp

class ActivitiesListAdapter(
    activity: FragmentActivity,
    private val application: ApplicationModel,
) : ListAdapter<ActivityModel, ActivitiesListAdapter.ViewHolder>(DIFF_CALLBACK) {

    private val glide = GlideApp.with(activity)
    @ColorInt
    private val textColorPrimary = activity.resolveColorAttr(android.R.attr.textColorPrimary)

    var itemClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemActivityBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(application, getItem(position), glide, textColorPrimary, itemClickListener)
    }

    interface ItemClickListener {

        fun onItemClick(item: ActivityModel)

        fun onItemLongClick(item: ActivityModel)
    }

    class ViewHolder(
        private val binding: ItemActivityBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            application: ApplicationModel,
            item: ActivityModel,
            glide: RequestManager,
            @ColorInt textColorPrimary: Int,
            itemClickListener: ItemClickListener?,
        ) {
            binding.name.text = item.name
            binding.packageName.text = item.componentName.shortClassName
            binding.label.text = item.label
            binding.label.isVisible = !item.label.isNullOrBlank() &&
                item.label != application.name && item.label != item.name

            glide.load(item)
                .apply(RequestOptions().fitCenter())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.icon)

            val context = binding.root.context
            binding.name.setTextColor(
                if (item.exported) {
                    textColorPrimary
                } else {
                    ContextCompat.getColor(context, R.color.red)
                }
            )

            binding.root.setOnClickListener {
                itemClickListener?.onItemClick(item)
            }
            binding.root.setOnLongClickListener {
                itemClickListener?.onItemLongClick(item)
                true
            }
            binding.appMenu.setOnClickListener {
                itemClickListener?.onItemLongClick(item)
            }
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
