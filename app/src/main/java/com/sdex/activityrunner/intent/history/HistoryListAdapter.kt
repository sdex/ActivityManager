package com.sdex.activityrunner.intent.history

import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sdex.activityrunner.R
import com.sdex.activityrunner.databinding.ItemHistoryBinding
import com.sdex.activityrunner.db.history.HistoryModel
import com.sdex.activityrunner.intent.param.None

class HistoryListAdapter(
    private val callback: Callback
) : ListAdapter<HistoryModel, HistoryListAdapter.ViewHolder>(DIFF_CALLBACK) {

    var contextMenuItemPosition: Int = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemHistoryBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), callback)
        holder.itemView.setOnLongClickListener {
            contextMenuItemPosition = holder.bindingAdapterPosition
            false
        }
    }

    override fun getItemId(position: Int): Long =
        getItem(position)?.id?.toLong() ?: 0L

    override fun onViewRecycled(holder: ViewHolder) {
        holder.itemView.setOnLongClickListener(null)
        super.onViewRecycled(holder)
    }

    interface Callback {

        fun onItemClicked(item: HistoryModel, position: Int)
    }

    class ViewHolder(
        private val binding: ItemHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root),
        View.OnCreateContextMenuListener {

        fun bind(item: HistoryModel, callback: Callback) {
            binding.packageName.text = getValueOrPlaceholder(item.packageName)
            binding.className.text = getValueOrPlaceholder(item.className)
            binding.action.text = getValueOrPlaceholder(item.action)
            binding.data.text = getValueOrPlaceholder(item.data)
            binding.mimeType.text = getValueOrPlaceholder(item.mimeType)
            binding.extras.setText(isNotEmpty(item.extras))
            binding.categories.setText(isNotEmpty(item.categories))
            binding.flags.setText(isNotEmpty(item.flags))

            binding.root.setOnClickListener {
                callback.onItemClicked(item, bindingAdapterPosition)
            }
            binding.root.setOnCreateContextMenuListener(this)
        }

        private fun isNotEmpty(value: String?): Int {
            return if (value.isNullOrEmpty()) R.string.no else R.string.yes
        }

        private fun getValueOrPlaceholder(value: String?): String {
            return if (value.isNullOrEmpty()) None.VALUE else value
        }

        override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
            menu.setHeaderTitle(R.string.history_item_dialog_title)
            menu.add(
                Menu.NONE, MENU_ITEM_ADD_SHORTCUT, Menu.NONE,
                R.string.history_item_dialog_add_shortcut
            )
            menu.add(
                Menu.NONE, MENU_ITEM_EXPORT_URI, Menu.NONE,
                R.string.history_item_dialog_export_uri
            )
            menu.add(
                Menu.NONE, MENU_ITEM_REMOVE, Menu.NONE,
                R.string.history_item_dialog_remove
            )
        }
    }

    companion object {

        const val MENU_ITEM_REMOVE = 0
        const val MENU_ITEM_ADD_SHORTCUT = 1
        const val MENU_ITEM_EXPORT_URI = 2

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HistoryModel>() {

            override fun areItemsTheSame(oldItem: HistoryModel, newItem: HistoryModel) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: HistoryModel, newItem: HistoryModel) =
                oldItem == newItem
        }
    }
}
