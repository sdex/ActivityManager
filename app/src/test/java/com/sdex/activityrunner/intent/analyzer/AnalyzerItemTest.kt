package com.sdex.activityrunner.intent.analyzer

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AnalyzerItemTest {

    private val items = listOf(
        AnalyzerItem.Section("General"),
        AnalyzerItem.Field("Action", "android.intent.action.SEND"),
        AnalyzerItem.Section("Flags"),
        AnalyzerItem.Value("FLAG_ACTIVITY_NEW_TASK"),
        AnalyzerItem.Value("FLAG_GRANT_READ_URI_PERMISSION"),
        AnalyzerItem.Section("Handlers", collapsible = true),
        AnalyzerItem.Field("Messages", "com.example.sms/.Share"),
        AnalyzerItem.Field("Notes", "com.example.notes/.Share"),
    )

    @Test
    fun `nothing is folded without a collapsed section`() {
        assertThat(items.foldSections(emptySet())).isEqualTo(items)
    }

    @Test
    fun `a folded section keeps its header and drops its rows`() {
        val folded = items.foldSections(setOf("Handlers"))

        assertThat(folded).hasSize(6)
        assertThat(folded.last()).isEqualTo(AnalyzerItem.Section("Handlers", collapsible = true))
    }

    @Test
    fun `folding one section leaves the others alone`() {
        val folded = items.foldSections(setOf("Flags"))

        assertThat(folded.filterIsInstance<AnalyzerItem.Value>()).isEmpty()
        assertThat(folded.filterIsInstance<AnalyzerItem.Field>()).hasSize(3)
    }

    @Test
    fun `every section can be folded at once`() {
        val folded = items.foldSections(setOf("General", "Flags", "Handlers"))

        assertThat(folded).isEqualTo(items.filterIsInstance<AnalyzerItem.Section>())
    }

    @Test
    fun `an unknown title folds nothing`() {
        assertThat(items.foldSections(setOf("Extras"))).isEqualTo(items)
    }

    @Test
    fun `rows are counted per section`() {
        assertThat(items.rowCountsPerSection()).containsExactly(
            "General", 1,
            "Flags", 2,
            "Handlers", 2,
        )
    }

    @Test
    fun `an empty section counts zero`() {
        val counts = listOf(AnalyzerItem.Section("Handlers")).rowCountsPerSection()

        assertThat(counts).containsExactly("Handlers", 0)
    }
}
