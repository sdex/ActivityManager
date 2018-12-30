package com.sdex.activityrunner.intent.history

import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import com.sdex.activityrunner.R
import com.sdex.activityrunner.db.history.HistoryModel
import com.sdex.activityrunner.intent.param.None
import kotlinx.android.synthetic.main.item_history.view.*

class HistoryListAdapter(private val callback: HistoryListAdapter.Callback)
  : PagedListAdapter<HistoryModel, HistoryListAdapter.ViewHolder>(DIFF_CALLBACK) {

  var contextMenuItemPosition: Int = 0

  override fun onCreateViewHolder(parent: ViewGroup,
                                  viewType: Int): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val view = inflater.inflate(R.layout.item_history, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(getItem(position), callback)
    holder.itemView.setOnLongClickListener {
      contextMenuItemPosition = holder.adapterPosition
      false
    }
  }

  public override fun getItem(position: Int): HistoryModel? {
    return super.getItem(position)
  }

  override fun getItemId(position: Int): Long {
    return if (getItem(position) != null) getItem(position)!!.id.toLong() else 0
  }

  override fun onViewRecycled(holder: ViewHolder) {
    holder.itemView.setOnLongClickListener(null)
    super.onViewRecycled(holder)
  }

  interface Callback {

    fun onItemClicked(item: HistoryModel, position: Int)
  }

  class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView),
    View.OnCreateContextMenuListener {

    fun bind(item: HistoryModel?, callback: Callback) {
      if (item != null) {
        itemView.packageName.text = getValueOrPlaceholder(item.packageName)
        itemView.className.text = getValueOrPlaceholder(item.className)
        itemView.action.text = getValueOrPlaceholder(item.action)
        itemView.data.text = getValueOrPlaceholder(item.data)
        itemView.mimeType.text = getValueOrPlaceholder(item.mimeType)
        itemView.extras.setText(isNotEmpty(item.extras))
        itemView.categories.setText(isNotEmpty(item.categories))
        itemView.flags.setText(isNotEmpty(item.flags))

        itemView.setOnClickListener { callback.onItemClicked(item, adapterPosition) }
        itemView.setOnCreateContextMenuListener(this)
      }
    }

    private fun isNotEmpty(value: String?): Int {
      return if (value.isNullOrEmpty()) R.string.no else R.string.yes
    }

    private fun getValueOrPlaceholder(value: String?): String {
      return if (value.isNullOrEmpty()) None.VALUE else value
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
      menu.setHeaderTitle(R.string.history_item_dialog_title)
      menu.add(Menu.NONE, MENU_ITEM_ADD_SHORTCUT, Menu.NONE,
        R.string.history_item_dialog_add_shortcut)
      menu.add(Menu.NONE, MENU_ITEM_EXPORT_URI, Menu.NONE,
        R.string.history_item_dialog_export_uri)
      menu.add(Menu.NONE, MENU_ITEM_REMOVE, Menu.NONE,
        R.string.history_item_dialog_remove)
    }
  }

  companion object {

    const val MENU_ITEM_REMOVE = 0
    const val MENU_ITEM_ADD_SHORTCUT = 1
    const val MENU_ITEM_EXPORT_URI = 2

    val DIFF_CALLBACK: DiffUtil.ItemCallback<HistoryModel> = object : DiffUtil.ItemCallback<HistoryModel>() {

      override fun areItemsTheSame(oldItem: HistoryModel, newItem: HistoryModel): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: HistoryModel, newItem: HistoryModel): Boolean {
        return oldItem == newItem
      }
    }
  }
}
