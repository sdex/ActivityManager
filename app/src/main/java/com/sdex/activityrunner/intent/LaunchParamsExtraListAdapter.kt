package com.sdex.activityrunner.intent

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sdex.activityrunner.R
import kotlinx.android.synthetic.main.item_launch_param_extra.view.*
import java.util.*

class LaunchParamsExtraListAdapter(private val callback: Callback)
  : RecyclerView.Adapter<LaunchParamsExtraListAdapter.ViewHolder>() {

  private var items: List<LaunchParamsExtra> = ArrayList()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val view = inflater.inflate(R.layout.item_launch_param_extra, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val item = items[position]
    holder.bind(item, callback)
  }

  override fun getItemCount(): Int {
    return items.size
  }

  fun setItems(items: List<LaunchParamsExtra>) {
    this.items = items
    notifyDataSetChanged()
  }

  interface Callback {

    fun onItemSelected(position: Int)

    fun removeItem(position: Int)
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: LaunchParamsExtra, callback: Callback) {
      itemView.key.text = item.key
      itemView.value.text = item.value
      itemView.setOnClickListener { callback.onItemSelected(adapterPosition) }
      itemView.remove.setOnClickListener { callback.removeItem(adapterPosition) }
    }
  }
}
