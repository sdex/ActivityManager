package com.sdex.activityrunner.intent

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sdex.activityrunner.R
import com.sdex.activityrunner.intent.param.None

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

    override fun getItemCount() = items.size

    fun setItems(items: Collection<String>?, showEmpty: Boolean = false) {
        this.items.clear()
        if (items.isNullOrEmpty()) {
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
