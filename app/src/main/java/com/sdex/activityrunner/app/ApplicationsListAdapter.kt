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
import com.sdex.activityrunner.util.ApplicationSectionNameProvider
import com.sdex.activityrunner.util.EmptySectionNameProvider
import com.sdex.activityrunner.util.SectionNameProvider
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView

class ApplicationsListAdapter(
    activity: FragmentActivity,
    private val appPreferences: AppPreferences,
) : ListAdapter<ApplicationModel, ApplicationsListAdapter.AppViewHolder>(DIFF_CALLBACK),
    FastScrollRecyclerView.SectionedAdapter {

    private val glide = Glide.with(activity)

    private var showSystemAppIndicator: Boolean = appPreferences.isShowSystemAppIndicator
    private var showDisabledAppIndicator: Boolean = appPreferences.isShowDisabledAppIndicator
    private var sectionNameProvider: SectionNameProvider =
        if (appPreferences.sortBy == ApplicationModel.NAME) {
            ApplicationSectionNameProvider
        } else {
            EmptySectionNameProvider
        }
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

    override fun getSectionName(position: Int): String =
        sectionNameProvider.getSectionName(getItem(position))

    @SuppressLint("NotifyDataSetChanged")
    fun update() {
        sectionNameProvider = if (appPreferences.sortBy == ApplicationModel.NAME) {
            ApplicationSectionNameProvider
        } else {
            EmptySectionNameProvider
        }
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
            binding.info.isVisible = showDisabledLabel || showSystemLabel
            binding.system.isVisible = showSystemLabel
            binding.disabled.isVisible = showDisabledLabel

            val context = binding.root.context
            binding.version.text = context.getString(
                R.string.app_version_format,
                item.versionName,
                item.versionCode
            )

            val totalActivitiesFormattedText = context.resources.getQuantityString(
                R.plurals.activities_count,
                item.activitiesCount,
                item.activitiesCount,
            )
            binding.activities.text = context.getString(
                R.string.app_info_activities_number,
                totalActivitiesFormattedText,
                item.exportedActivitiesCount,
            )

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
