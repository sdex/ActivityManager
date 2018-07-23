package com.sdex.activityrunner.intent

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.sdex.activityrunner.R
import com.sdex.activityrunner.intent.param.None
import java.util.*

class LaunchParamsListAdapter : RecyclerView.Adapter<LaunchParamsListAdapter.ViewHolder>() {

  private val items = ArrayList<String>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val v = inflater.inflate(R.layout.item_launch_param, parent, false) as TextView
    return ViewHolder(v)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.textView.text = items[position]
  }

  override fun getItemCount(): Int {
    return items.size
  }

  fun setItems(items: Collection<String>?, showEmpty: Boolean = false) {
    this.items.clear()
    if (items == null || items.isEmpty()) {
      if (showEmpty) {
        this.items.add(None.VALUE)
      }
    } else {
      this.items.addAll(items)
    }
    notifyDataSetChanged()
  }

  class ViewHolder(var textView: TextView) : RecyclerView.ViewHolder(textView)
}
