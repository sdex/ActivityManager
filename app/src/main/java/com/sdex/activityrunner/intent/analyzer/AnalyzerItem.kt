package com.sdex.activityrunner.intent.analyzer

/**
 * A single row of the intent analyzer screen. The analyzer produces a flat list of these so a
 * section can hold any number of rows without the layout having to know about it upfront.
 */
sealed interface AnalyzerItem {

    /**
     * Group header, e.g. "Extras". A [collapsible] section can be folded away by tapping it and
     * starts folded, for groups that are long enough to bury everything below them.
     */
    data class Section(
        val title: String,
        val collapsible: Boolean = false,
    ) : AnalyzerItem

    /**
     * Labelled value. [type] is the runtime type of the value when it is worth showing, e.g. the
     * class of an extra.
     */
    data class Field(
        val label: String,
        val value: String,
        val type: String? = null,
    ) : AnalyzerItem

    /** Standalone value that needs no label, e.g. a single category or flag. */
    data class Value(val value: String) : AnalyzerItem
}

/** Drops the rows of every section named in [collapsed], keeping the headers themselves. */
fun List<AnalyzerItem>.foldSections(collapsed: Set<String>): List<AnalyzerItem> {
    val result = ArrayList<AnalyzerItem>(size)
    var hidden = false
    for (item in this) {
        if (item is AnalyzerItem.Section) {
            hidden = item.title in collapsed
            result.add(item)
        } else if (!hidden) {
            result.add(item)
        }
    }
    return result
}

/** How many rows each section holds, so a folded header can still say how much it hides. */
fun List<AnalyzerItem>.rowCountsPerSection(): Map<String, Int> {
    val counts = mutableMapOf<String, Int>()
    var title: String? = null
    for (item in this) {
        if (item is AnalyzerItem.Section) {
            title = item.title
            counts[title] = 0
        } else if (title != null) {
            counts[title] = counts.getValue(title) + 1
        }
    }
    return counts
}
