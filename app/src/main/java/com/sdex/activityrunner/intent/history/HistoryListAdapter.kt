package com.sdex.activityrunner.intent.history

import android.support.v7.widget.RecyclerView
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import com.sdex.activityrunner.R
import com.sdex.activityrunner.db.history.HistoryModel
import com.sdex.activityrunner.intent.dialog.source.ActionSource
import com.sdex.activityrunner.intent.dialog.source.MimeTypeSource
import com.sdex.activityrunner.intent.param.None
import kotlinx.android.synthetic.main.item_history.view.*
import java.util.*

class HistoryListAdapter(private val callback: HistoryListAdapter.Callback)
  : RecyclerView.Adapter<HistoryListAdapter.ViewHolder>() {

  private var items: List<HistoryModel> = ArrayList()
  var contextMenuItemPosition: Int = 0

  override fun onCreateViewHolder(parent: ViewGroup,
                                  viewType: Int): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val view = inflater.inflate(R.layout.item_history, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val item = items[position]
    holder.bind(item, callback)
    holder.itemView.setOnLongClickListener {
      contextMenuItemPosition = holder.adapterPosition
      false
    }
  }

  override fun getItemCount(): Int {
    return items.size
  }

  override fun getItemId(position: Int): Long {
    return items[position].id.toLong()
  }

  override fun onViewRecycled(holder: ViewHolder) {
    holder.itemView.setOnLongClickListener(null)
    super.onViewRecycled(holder)
  }

  fun getItem(position: Int): HistoryModel {
    return items[position]
  }

  fun setItems(items: List<HistoryModel>) {
    this.items = items
    notifyDataSetChanged()
  }

  interface Callback {

    fun onItemClicked(item: HistoryModel, position: Int)
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    View.OnCreateContextMenuListener {

    private val actionSource = ActionSource()
    private val mimeTypeSource = MimeTypeSource()

    fun bind(item: HistoryModel, callback: Callback) {
      itemView.packageName.text = getValueOrPlaceholder(item.packageName)
      itemView.className.text = getValueOrPlaceholder(item.className)
      itemView.action.text = actionSource.getItem(item.action)
      itemView.data.text = getValueOrPlaceholder(item.data)
      itemView.mimeType.text = mimeTypeSource.getItem(item.mimeType)
      itemView.extras.setText(isNotEmpty(item.extras))
      itemView.categories.setText(isNotEmpty(item.categories))
      itemView.flags.setText(isNotEmpty(item.flags))

      itemView.setOnClickListener { callback.onItemClicked(item, adapterPosition) }
      itemView.setOnCreateContextMenuListener(this)
    }

    private fun isNotEmpty(value: String?): Int {
      return if (value.isNullOrBlank()) R.string.no else R.string.yes
    }

    private fun getValueOrPlaceholder(value: String?): String {
      return if (value.isNullOrBlank()) None.VALUE else value!!
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo) {
      menu.setHeaderTitle(R.string.history_item_dialog_title)
      menu.add(Menu.NONE, MENU_ITEM_ADD_SHORTCUT, Menu.NONE,
        R.string.history_item_dialog_add_shortcut)
      menu.add(Menu.NONE, MENU_ITEM_REMOVE, Menu.NONE,
        R.string.history_item_dialog_remove)
    }
  }

  companion object {

    const val MENU_ITEM_REMOVE = 0
    const val MENU_ITEM_ADD_SHORTCUT = 1
  }
}
