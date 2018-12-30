package com.sdex.activityrunner.intent

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sdex.activityrunner.R
import kotlinx.android.synthetic.main.item_launch_param_extra.view.*
import java.util.*

class LaunchParamsExtraListAdapter : RecyclerView.Adapter<LaunchParamsExtraListAdapter.ViewHolder>() {

  private var items: List<LaunchParamsExtra> = ArrayList()
  private var viewMode = false
  var callback: Callback? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val view = inflater.inflate(R.layout.item_launch_param_extra, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val item = items[position]
    holder.bind(item, callback, viewMode)
  }

  override fun getItemCount(): Int {
    return items.size
  }

  fun setItems(items: List<LaunchParamsExtra>, viewMode: Boolean = false) {
    this.items = items
    this.viewMode = viewMode
    notifyDataSetChanged()
  }

  interface Callback {

    fun onItemSelected(position: Int)

    fun removeItem(position: Int)
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: LaunchParamsExtra, callback: Callback?, viewMode: Boolean) {
      itemView.key.text = item.key
      itemView.value.text = item.value
      if (viewMode) {
        itemView.setOnClickListener(null)
        itemView.remove.visibility = GONE
      } else {
        itemView.setOnClickListener { callback?.onItemSelected(adapterPosition) }
        itemView.remove.setOnClickListener { callback?.removeItem(adapterPosition) }
        itemView.remove.visibility = VISIBLE
      }
    }
  }
}
