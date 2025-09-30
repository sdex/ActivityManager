package com.sdex.activityrunner.intent

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.sdex.activityrunner.databinding.ItemLaunchParamExtraBinding

class LaunchParamsExtraListAdapter :
    RecyclerView.Adapter<LaunchParamsExtraListAdapter.ViewHolder>() {

    private var items: List<LaunchParamsExtra> = ArrayList()
    private var viewMode = false
    var callback: Callback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemLaunchParamExtraBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, callback, viewMode)
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<LaunchParamsExtra>, viewMode: Boolean = false) {
        this.items = items
        this.viewMode = viewMode
        notifyDataSetChanged()
    }

    interface Callback {

        fun onItemSelected(position: Int)

        fun removeItem(position: Int)
    }

    class ViewHolder(
        private val binding: ItemLaunchParamExtraBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LaunchParamsExtra, callback: Callback?, viewMode: Boolean) {
            binding.key.text = item.key
            binding.value.text = item.value
            binding.remove.isVisible = !viewMode
            if (viewMode) {
                binding.root.setOnClickListener(null)
            } else {
                binding.root.setOnClickListener {
                    callback?.onItemSelected(bindingAdapterPosition)
                }
                binding.remove.setOnClickListener {
                    callback?.removeItem(bindingAdapterPosition)
                }
            }
        }
    }
}
