package com.sdex.activityrunner.intent.analyzer

import android.content.ClipData
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import android.os.Bundle
import androidx.core.net.toUri
import com.google.common.truth.Truth.assertThat
import com.sdex.activityrunner.R
import com.sdex.activityrunner.intent.param.None
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class IntentAnalyzerTest {

    private val context: Context = RuntimeEnvironment.getApplication()
    private val analyzer = IntentAnalyzer(context)

    @Test
    fun `empty intent still reports the core fields`() {
        val items = analyzer.analyze(Intent())

        assertThat(valueOf(items, R.string.launch_param_action)).isEqualTo(None.VALUE)
        assertThat(valueOf(items, R.string.launch_param_data)).isEqualTo(None.VALUE)
        assertThat(valueOf(items, R.string.launch_param_mime_type)).isEqualTo(None.VALUE)
        assertThat(sections(items)).containsAtLeast(
            string(R.string.analyzer_section_general),
            string(R.string.launch_param_categories),
            string(R.string.launch_param_flags),
            string(R.string.launch_param_extras),
        ).inOrder()
    }

    @Test
    fun `general section reports the component and the source`() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            // the package has to be set before the component, the platform rejects the other order
            setPackage("com.example.app")
            component = ComponentName("com.example.app", "com.example.app.MainActivity")
            type = "text/plain"
        }
        val source = IntentSource(
            referrer = "android-app://com.example.sender".toUri(),
            callingPackage = "com.example.sender",
            callingActivity = ComponentName("com.example.sender", ".Main"),
        )

        val items = analyzer.analyze(intent, source)

        assertThat(valueOf(items, R.string.launch_param_action)).isEqualTo(Intent.ACTION_SEND)
        assertThat(valueOf(items, R.string.analyzer_label_component))
            .isEqualTo("com.example.app/.MainActivity")
        assertThat(valueOf(items, R.string.launch_param_package_name))
            .isEqualTo("com.example.app")
        assertThat(valueOf(items, R.string.analyzer_label_referrer))
            .isEqualTo("android-app://com.example.sender")
        assertThat(valueOf(items, R.string.analyzer_label_calling_package))
            .isEqualTo("com.example.sender")
    }

    @Test
    fun `uri section takes the data apart`() {
        val uri = "https://user@example.com:8443/a/b?q=1&q=2&lang=en#frag".toUri()
        val items = analyzer.analyze(Intent(Intent.ACTION_VIEW, uri))

        assertThat(sections(items)).contains(string(R.string.analyzer_section_uri))
        assertThat(valueOf(items, R.string.analyzer_label_scheme)).isEqualTo("https")
        assertThat(valueOf(items, R.string.analyzer_label_user_info)).isEqualTo("user")
        assertThat(valueOf(items, R.string.analyzer_label_host)).isEqualTo("example.com")
        assertThat(valueOf(items, R.string.analyzer_label_port)).isEqualTo("8443")
        assertThat(valueOf(items, R.string.analyzer_label_path)).isEqualTo("/a/b")
        assertThat(valueOf(items, R.string.analyzer_label_fragment)).isEqualTo("frag")
        assertThat(fields(items).filter { it.label == "q" }.map { it.value })
            .containsExactly("1", "2")
        assertThat(fields(items).single { it.label == "lang" }.value).isEqualTo("en")
    }

    @Test
    fun `uri section handles an opaque uri`() {
        val items = analyzer.analyze(Intent(Intent.ACTION_VIEW, "mailto:me@example.com".toUri()))

        assertThat(valueOf(items, R.string.analyzer_label_scheme)).isEqualTo("mailto")
        assertThat(valueOf(items, R.string.analyzer_label_scheme_specific_part))
            .isEqualTo("me@example.com")
        assertThat(labels(items)).doesNotContain(string(R.string.analyzer_label_host))
    }

    @Test
    fun `no uri section without data`() {
        val items = analyzer.analyze(Intent(Intent.ACTION_SEND))

        assertThat(sections(items)).doesNotContain(string(R.string.analyzer_section_uri))
    }

    @Test
    fun `categories and flags are listed`() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val items = analyzer.analyze(intent)

        assertThat(values(items)).contains(Intent.CATEGORY_BROWSABLE)
        assertThat(values(items)).contains("FLAG_ACTIVITY_NEW_TASK")
        assertThat(valueOf(items, R.string.analyzer_label_flags_raw))
            .isEqualTo(String.format("0x%08X", Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    @Test
    fun `empty categories and flags report none`() {
        val items = analyzer.analyze(Intent(Intent.ACTION_VIEW))

        assertThat(values(items).count { it == None.VALUE }).isEqualTo(3)
    }

    @Test
    fun `extras keep their runtime type`() {
        val intent = Intent().apply {
            putExtra("str", "text")
            putExtra("int", 42)
            putExtra("ints", intArrayOf(1, 2, 3))
            putExtra("bundle", Bundle().apply { putString("nested", "value") })
        }

        val extras = fields(analyzer.analyze(intent)).associateBy { it.label }

        assertThat(extras.getValue("str").value).isEqualTo("text")
        assertThat(extras.getValue("str").type).isEqualTo("String")
        assertThat(extras.getValue("int").value).isEqualTo("42")
        assertThat(extras.getValue("int").type).isEqualTo("Integer")
        assertThat(extras.getValue("ints").value).isEqualTo("[1, 2, 3]")
        assertThat(extras.getValue("ints").type).isEqualTo("int[]")
        assertThat(extras.getValue("bundle").value).isEqualTo("{nested=value}")
    }

    @Test
    fun `long arrays are capped`() {
        val intent = Intent().putExtra("bytes", ByteArray(1024) { 7 })

        val value = fields(analyzer.analyze(intent)).single { it.label == "bytes" }.value

        assertThat(value).endsWith(", …]")
        assertThat(value.split(",")).hasSize(257)
    }

    @Test
    fun `clip data items are listed`() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            clipData = ClipData.newPlainText("label", "shared text")
        }

        val items = analyzer.analyze(intent)

        assertThat(sections(items)).contains(string(R.string.analyzer_section_clip_data))
        assertThat(valueOf(items, R.string.analyzer_label_clip_label)).isEqualTo("label")
        val itemLabel = context.getString(
            R.string.analyzer_clip_item,
            1,
            string(R.string.analyzer_clip_text),
        )
        assertThat(fields(items).single { it.label == itemLabel }.value).isEqualTo("shared text")
    }

    @Test
    fun `selector is reported`() {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            selector = Intent(Intent.ACTION_VIEW).apply { setPackage("com.example.browser") }
        }

        val items = analyzer.analyze(intent)

        val selectorStart = items.indexOfFirst {
            it is AnalyzerItem.Section && it.title == string(R.string.analyzer_section_selector)
        }
        assertThat(selectorStart).isNotEqualTo(-1)
        val selectorItems = items.drop(selectorStart)
        assertThat(valueOf(selectorItems, R.string.launch_param_action))
            .isEqualTo(Intent.ACTION_VIEW)
        assertThat(valueOf(selectorItems, R.string.launch_param_package_name))
            .isEqualTo("com.example.browser")
    }

    @Test
    fun `handlers are listed in a section that starts folded`() {
        val intent = Intent(Intent.ACTION_VIEW, "https://example.com".toUri())
        val handler = ResolveInfo().apply {
            nonLocalizedLabel = "Example Browser"
            activityInfo = ActivityInfo().apply {
                packageName = "com.example.browser"
                name = "com.example.browser.MainActivity"
            }
        }
        shadowOf(context.packageManager).addResolveInfoForIntent(intent, handler)

        val items = analyzer.analyze(intent)

        val section = items.filterIsInstance<AnalyzerItem.Section>()
            .single { it.title == string(R.string.analyzer_section_handlers) }
        assertThat(section.collapsible).isTrue()
        assertThat(fields(items).single { it.label == "Example Browser" }.value)
            .isEqualTo("com.example.browser/.MainActivity")
    }

    @Test
    fun `sections other than handlers stay open`() {
        val items = analyzer.analyze(Intent(Intent.ACTION_SEND))

        assertThat(items.filterIsInstance<AnalyzerItem.Section>().filter { it.collapsible })
            .isEmpty()
    }

    @Test
    fun `intent uri is exported`() {
        val items = analyzer.analyze(Intent(Intent.ACTION_VIEW, "https://example.com".toUri()))

        assertThat(valueOf(items, R.string.analyzer_label_intent_uri)).startsWith("intent:")
    }

    @Test
    fun `unreadable extras degrade to a single row`() {
        val items = analyzer.analyze(UnreadableExtrasIntent())

        assertThat(labels(items)).contains(string(R.string.analyzer_extras_error))
    }

    private fun string(resId: Int) = context.getString(resId)

    private fun sections(items: List<AnalyzerItem>) =
        items.filterIsInstance<AnalyzerItem.Section>().map { it.title }

    private fun fields(items: List<AnalyzerItem>) = items.filterIsInstance<AnalyzerItem.Field>()

    private fun labels(items: List<AnalyzerItem>) = fields(items).map { it.label }

    private fun values(items: List<AnalyzerItem>) =
        items.filterIsInstance<AnalyzerItem.Value>().map { it.value }

    private fun valueOf(items: List<AnalyzerItem>, labelRes: Int): String? =
        fields(items).firstOrNull { it.label == string(labelRes) }?.value

    /** Stands in for a bundle carrying a parcelable whose class only the sender has. */
    private class UnreadableExtrasIntent : Intent() {

        override fun getExtras(): Bundle = mockk {
            every { keySet() } throws IllegalStateException("bad parcel")
        }
    }
}
