package com.sdex.activityrunner.app

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.sdex.activityrunner.R
import com.sdex.activityrunner.databinding.ItemApplicationBinding
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.preferences.AppPreferences
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView

class ApplicationsListAdapter(
    activity: FragmentActivity,
    private val appPreferences: AppPreferences,
) : ListAdapter<ApplicationModel, ApplicationsListAdapter.AppViewHolder>(DIFF_CALLBACK),
    FastScrollRecyclerView.SectionedAdapter {

    private val glide = Glide.with(activity)

    private var showSystemAppIndicator: Boolean = appPreferences.isShowSystemAppIndicator
    private var showDisabledAppIndicator: Boolean = appPreferences.isShowDisabledAppIndicator

    var itemClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return AppViewHolder(ItemApplicationBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(
            getItem(position),
            glide,
            showSystemAppIndicator,
            showDisabledAppIndicator,
            itemClickListener
        )
    }

    override fun getSectionName(position: Int): String {
        val name = getItem(position).name
        return if (name.isNullOrEmpty()) "" else name.first().uppercaseChar().toString()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update() {
        if (appPreferences.isShowSystemAppIndicator != showSystemAppIndicator ||
            appPreferences.isShowDisabledAppIndicator != showDisabledAppIndicator
        ) {
            showSystemAppIndicator = appPreferences.isShowSystemAppIndicator
            showDisabledAppIndicator = appPreferences.isShowDisabledAppIndicator
            notifyDataSetChanged()
        }
    }

    interface ItemClickListener {

        fun onItemClick(item: ApplicationModel)

        fun onItemLongClick(item: ApplicationModel)
    }

    class AppViewHolder(
        private val binding: ItemApplicationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: ApplicationModel,
            glide: RequestManager,
            showSystemAppIndicator: Boolean,
            showDisabledAppIndicator: Boolean,
            itemClickListener: ItemClickListener?,
        ) {
            binding.name.text = item.name
            binding.packageName.text = item.packageName
            val showSystemLabel = item.system && showSystemAppIndicator
            val showDisabledLabel = !item.enabled && showDisabledAppIndicator
            val info = StringBuilder()
            val context = binding.root.context
            if (showDisabledLabel) {
                info.append(context.getString(R.string.application_disabled))
            }
            if (showDisabledLabel && showSystemLabel) {
                info.append(" | ")
            }
            if (showSystemLabel) {
                info.append(context.getString(R.string.application_system))
            }
            binding.info.text = info.toString()
            binding.info.isVisible = showDisabledLabel || showSystemLabel
            glide.load(item)
                .apply(RequestOptions().fitCenter())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.icon)

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

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ApplicationModel>() {

            override fun areItemsTheSame(
                oldItem: ApplicationModel,
                newItem: ApplicationModel
            ): Boolean {
                return oldItem.packageName == newItem.packageName
            }

            override fun areContentsTheSame(
                oldItem: ApplicationModel,
                newItem: ApplicationModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
