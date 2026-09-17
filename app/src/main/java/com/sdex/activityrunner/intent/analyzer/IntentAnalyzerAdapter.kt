package com.sdex.activityrunner.intent.analyzer

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.sdex.activityrunner.R
import com.sdex.activityrunner.databinding.ItemAnalyzerFieldBinding
import com.sdex.activityrunner.databinding.ItemAnalyzerSectionBinding
import com.sdex.activityrunner.databinding.ItemAnalyzerValueBinding
import com.sdex.activityrunner.extensions.copyToClipboardOnLongClick

private const val TYPE_SECTION = 0
private const val TYPE_FIELD = 1
private const val TYPE_VALUE = 2

private const val ROTATION_COLLAPSED = -90f
private const val ROTATION_EXPANDED = 0f

class IntentAnalyzerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<AnalyzerItem> = emptyList()
    private var visibleItems: List<AnalyzerItem> = emptyList()
    private var rowCounts: Map<String, Int> = emptyMap()
    private val collapsedSections = mutableSetOf<String>()

    fun setItems(items: List<AnalyzerItem>) {
        // the state flow replays on every restart, re-applying the same rows would throw away
        // whichever sections the user has unfolded in the meantime
        if (this.items == items) return
        this.items = items
        rowCounts = items.rowCountsPerSection()
        collapsedSections.clear()
        items.filterIsInstance<AnalyzerItem.Section>()
            .filter { it.collapsible }
            .forEach { collapsedSections.add(it.title) }
        rebuild()
    }

    override fun getItemCount(): Int = visibleItems.size

    override fun getItemViewType(position: Int): Int = when (visibleItems[position]) {
        is AnalyzerItem.Section -> TYPE_SECTION
        is AnalyzerItem.Field -> TYPE_FIELD
        is AnalyzerItem.Value -> TYPE_VALUE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_SECTION -> SectionViewHolder(
                ItemAnalyzerSectionBinding.inflate(inflater, parent, false),
            )

            TYPE_FIELD -> FieldViewHolder(
                ItemAnalyzerFieldBinding.inflate(inflater, parent, false),
            )

            else -> ValueViewHolder(
                ItemAnalyzerValueBinding.inflate(inflater, parent, false),
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = visibleItems[position]) {
            is AnalyzerItem.Section -> (holder as SectionViewHolder).bind(item)
            is AnalyzerItem.Field -> (holder as FieldViewHolder).bind(item)
            is AnalyzerItem.Value -> (holder as ValueViewHolder).bind(item)
        }
    }

    private fun toggle(title: String) {
        if (!collapsedSections.remove(title)) {
            collapsedSections.add(title)
        }
        rebuild()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun rebuild() {
        visibleItems = items.foldSections(collapsedSections)
        notifyDataSetChanged()
    }

    inner class SectionViewHolder(
        private val binding: ItemAnalyzerSectionBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val item = visibleItems.getOrNull(bindingAdapterPosition)
                if (item is AnalyzerItem.Section) {
                    toggle(item.title)
                }
            }
        }

        fun bind(item: AnalyzerItem.Section) {
            val context = binding.root.context
            binding.title.text = if (item.collapsible) {
                context.getString(
                    R.string.analyzer_section_count,
                    item.title,
                    rowCounts[item.title] ?: 0,
                )
            } else {
                item.title
            }
            binding.root.isClickable = item.collapsible
            binding.expand.isVisible = item.collapsible
            binding.expand.rotation = if (item.title in collapsedSections) {
                ROTATION_COLLAPSED
            } else {
                ROTATION_EXPANDED
            }
        }
    }

    class FieldViewHolder(
        private val binding: ItemAnalyzerFieldBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.value.copyToClipboardOnLongClick()
        }

        fun bind(item: AnalyzerItem.Field) {
            binding.label.text = item.label
            binding.value.text = item.value
            binding.type.text = item.type
            binding.type.isVisible = item.type != null
        }
    }

    class ValueViewHolder(
        private val binding: ItemAnalyzerValueBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.copyToClipboardOnLongClick()
        }

        fun bind(item: AnalyzerItem.Value) {
            binding.root.text = item.value
        }
    }
}
