package com.sdex.activityrunner.intent.analyzer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.StringRes
import com.sdex.activityrunner.R
import com.sdex.activityrunner.extensions.getFlagsList
import com.sdex.activityrunner.intent.param.None
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

/** Activity level context of an incoming intent, none of which is stored on the intent itself. */
data class IntentSource(
    val referrer: Uri? = null,
    val callingPackage: String? = null,
    val callingActivity: ComponentName? = null,
)

/**
 * Dumps everything the platform exposes about an incoming [Intent] into the flat row list rendered
 * by [IntentAnalyzerActivity].
 *
 * Intents intercepted from other applications are hostile input as far as this screen is concerned:
 * extras may carry parcelables whose classes we cannot load, and URIs may be malformed. Every read
 * that can throw is therefore guarded so a single bad value degrades to one row instead of crashing
 * the screen.
 */
class IntentAnalyzer @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {

    fun analyze(intent: Intent, source: IntentSource = IntentSource()): List<AnalyzerItem> {
        val items = mutableListOf<AnalyzerItem>()
        items.addGeneral(intent, source)
        items.addUri(intent)
        items.addCategories(intent)
        items.addFlags(intent)
        items.addExtras(intent)
        items.addClipData(intent)
        items.addSelector(intent)
        items.addExport(intent)
        items.addHandlers(intent)
        return items
    }

    private fun MutableList<AnalyzerItem>.addGeneral(intent: Intent, source: IntentSource) {
        section(R.string.analyzer_section_general)
        field(R.string.launch_param_action, intent.action, showWhenMissing = true)
        field(R.string.launch_param_data, intent.dataString, showWhenMissing = true)
        field(R.string.launch_param_mime_type, intent.type, showWhenMissing = true)
        val resolvedType = runCatching { intent.resolveType(context) }.getOrNull()
        if (resolvedType != null && resolvedType != intent.type) {
            field(R.string.analyzer_label_resolved_mime_type, resolvedType)
        }
        field(R.string.analyzer_label_component, intent.component?.flattenToShortString())
        field(R.string.launch_param_package_name, intent.`package`)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            field(R.string.analyzer_label_identifier, intent.identifier)
        }
        field(R.string.analyzer_label_referrer, source.referrer?.toString())
        field(R.string.analyzer_label_calling_package, source.callingPackage)
        field(
            R.string.analyzer_label_calling_activity,
            source.callingActivity?.flattenToShortString(),
        )
        field(R.string.analyzer_label_source_bounds, intent.sourceBounds?.toShortString())
    }

    private fun MutableList<AnalyzerItem>.addUri(intent: Intent) {
        val uri = intent.data ?: return
        section(R.string.analyzer_section_uri)
        field(R.string.analyzer_label_scheme, uri.scheme)
        if (uri.isOpaque) {
            field(R.string.analyzer_label_scheme_specific_part, uri.schemeSpecificPart)
        } else {
            field(R.string.analyzer_label_authority, uri.authority)
            field(R.string.analyzer_label_user_info, uri.userInfo)
            field(R.string.analyzer_label_host, uri.host)
            if (uri.port != -1) {
                field(R.string.analyzer_label_port, uri.port.toString())
            }
            field(R.string.analyzer_label_path, uri.path)
            field(R.string.analyzer_label_query, uri.query)
            addQueryParameters(uri)
        }
        field(R.string.analyzer_label_fragment, uri.fragment)
    }

    private fun MutableList<AnalyzerItem>.addQueryParameters(uri: Uri) {
        val names = runCatching { uri.queryParameterNames }.getOrNull().orEmpty()
        val type = context.getString(R.string.analyzer_type_query_parameter)
        for (name in names) {
            val values = runCatching { uri.getQueryParameters(name) }.getOrNull().orEmpty()
            for (value in values) {
                add(AnalyzerItem.Field(name, value, type))
            }
        }
    }

    private fun MutableList<AnalyzerItem>.addCategories(intent: Intent) {
        section(R.string.launch_param_categories)
        val categories = intent.categories
        if (categories.isNullOrEmpty()) {
            add(AnalyzerItem.Value(None.VALUE))
        } else {
            categories.forEach { add(AnalyzerItem.Value(it)) }
        }
    }

    private fun MutableList<AnalyzerItem>.addFlags(intent: Intent) {
        section(R.string.launch_param_flags)
        add(
            AnalyzerItem.Field(
                context.getString(R.string.analyzer_label_flags_raw),
                String.format(Locale.ROOT, "0x%08X", intent.flags),
            ),
        )
        val flags = intent.getFlagsList()
        if (flags.isEmpty()) {
            add(AnalyzerItem.Value(None.VALUE))
        } else {
            flags.forEach { add(AnalyzerItem.Value(it)) }
        }
    }

    private fun MutableList<AnalyzerItem>.addExtras(intent: Intent) {
        section(R.string.launch_param_extras)
        val extras = intent.extras
        // Reading the keys unparcels the bundle, which throws when an extra references a class that
        // only the sending application has.
        val keys = try {
            extras?.keySet()
        } catch (e: Exception) {
            Timber.w(e, "Failed to read the extras")
            add(
                AnalyzerItem.Field(
                    context.getString(R.string.analyzer_extras_error),
                    e.toString(),
                ),
            )
            return
        }
        if (extras == null || keys.isNullOrEmpty()) {
            add(AnalyzerItem.Value(None.VALUE))
            return
        }
        for (key in keys) {
            @Suppress("DEPRECATION")
            val value = runCatching { extras.get(key) }.getOrNull()
            add(AnalyzerItem.Field(key, formatValue(value), typeName(value)))
        }
    }

    private fun MutableList<AnalyzerItem>.addClipData(intent: Intent) {
        val clipData = intent.clipData ?: return
        section(R.string.analyzer_section_clip_data)
        val description = clipData.description
        field(R.string.analyzer_label_clip_label, description?.label?.toString())
        if (description != null) {
            val mimeTypes = (0 until description.mimeTypeCount).map { description.getMimeType(it) }
            field(R.string.analyzer_label_clip_mime_types, mimeTypes.joinToString(", "))
        }
        for (index in 0 until clipData.itemCount) {
            val item = runCatching { clipData.getItemAt(index) }.getOrNull() ?: continue
            val number = index + 1
            clipItem(number, R.string.analyzer_clip_text, item.text?.toString())
            clipItem(number, R.string.analyzer_label_html, item.htmlText)
            clipItem(number, R.string.analyzer_label_uri, item.uri?.toString())
            clipItem(number, R.string.analyzer_label_intent, item.intent?.let { describe(it) })
        }
    }

    private fun MutableList<AnalyzerItem>.clipItem(
        number: Int,
        @StringRes labelRes: Int,
        value: String?,
    ) {
        if (value == null) return
        val label = context.getString(
            R.string.analyzer_clip_item,
            number,
            context.getString(labelRes),
        )
        add(AnalyzerItem.Field(label, value))
    }

    private fun MutableList<AnalyzerItem>.addSelector(intent: Intent) {
        val selector = intent.selector ?: return
        section(R.string.analyzer_section_selector)
        field(R.string.launch_param_action, selector.action, showWhenMissing = true)
        field(R.string.launch_param_data, selector.dataString)
        field(R.string.launch_param_mime_type, selector.type)
        field(R.string.launch_param_package_name, selector.`package`)
        selector.categories?.forEach { add(AnalyzerItem.Value(it)) }
    }

    private fun MutableList<AnalyzerItem>.addExport(intent: Intent) {
        val uri = runCatching { intent.toUri(Intent.URI_INTENT_SCHEME) }.getOrNull() ?: return
        section(R.string.analyzer_section_export)
        add(AnalyzerItem.Field(context.getString(R.string.analyzer_label_intent_uri), uri))
    }

    /**
     * Lists the activities the system would offer for this intent. The probe intent carries only
     * the fields that take part in resolution, which keeps the extras out of the binder call.
     */
    private fun MutableList<AnalyzerItem>.addHandlers(intent: Intent) {
        if (intent.action == null && intent.data == null && intent.type == null) return
        val probe = Intent(intent.action).apply {
            setDataAndType(intent.data, intent.type)
            intent.categories?.forEach { addCategory(it) }
        }
        val packageManager = context.packageManager
        val resolved = try {
            packageManager.queryIntentActivities(probe, PackageManager.MATCH_DEFAULT_ONLY)
        } catch (e: Exception) {
            Timber.w(e, "Failed to resolve the intent")
            return
        }
        if (resolved.isEmpty()) return
        // a common intent resolves to dozens of activities, too many to sit open by default
        section(R.string.analyzer_section_handlers, collapsible = true)
        for (info in resolved) {
            val activityInfo = info.activityInfo ?: continue
            val label = runCatching { info.loadLabel(packageManager).toString() }
                .getOrNull()
                ?.takeIf { it.isNotBlank() }
                ?: activityInfo.packageName
            add(
                AnalyzerItem.Field(
                    label,
                    ComponentName(activityInfo.packageName, activityInfo.name)
                        .flattenToShortString(),
                ),
            )
        }
    }

    private fun MutableList<AnalyzerItem>.section(
        @StringRes titleRes: Int,
        collapsible: Boolean = false,
    ) {
        add(AnalyzerItem.Section(context.getString(titleRes), collapsible))
    }

    private fun MutableList<AnalyzerItem>.field(
        @StringRes labelRes: Int,
        value: String?,
        showWhenMissing: Boolean = false,
    ) {
        if (value.isNullOrEmpty() && !showWhenMissing) return
        val text = if (value.isNullOrEmpty()) None.VALUE else value
        add(AnalyzerItem.Field(context.getString(labelRes), text))
    }

    private fun describe(intent: Intent): String =
        runCatching { intent.toUri(Intent.URI_INTENT_SCHEME) }.getOrElse { intent.toString() }

    private fun typeName(value: Any?): String = value?.javaClass?.simpleName ?: NULL

    /**
     * Renders a value for display. Collections are expanded so their contents are visible, but
     * capped at [MAX_ELEMENTS] because an intent may legitimately carry a multi megabyte array and
     * a `TextView` is not the place to find that out.
     */
    private fun formatValue(value: Any?): String = when (value) {
        null -> NULL
        is BooleanArray -> value.asIterable().joinElements()
        is ByteArray -> value.asIterable().joinElements()
        is CharArray -> value.asIterable().joinElements()
        is ShortArray -> value.asIterable().joinElements()
        is IntArray -> value.asIterable().joinElements()
        is LongArray -> value.asIterable().joinElements()
        is FloatArray -> value.asIterable().joinElements()
        is DoubleArray -> value.asIterable().joinElements()
        is Array<*> -> value.asIterable().joinElements()
        is Iterable<*> -> value.joinElements()
        is Bundle -> formatBundle(value)
        is Intent -> describe(value)
        else -> value.toString()
    }

    private fun Iterable<*>.joinElements(): String {
        val elements = take(MAX_ELEMENTS + 1)
        val overflow = elements.size > MAX_ELEMENTS
        val shown = if (overflow) elements.take(MAX_ELEMENTS) else elements
        val suffix = if (overflow) ", …" else ""
        return shown.joinToString(", ", "[", "$suffix]") { formatValue(it) }
    }

    private fun formatBundle(bundle: Bundle): String = try {
        bundle.keySet().joinToString(", ", "{", "}") { key ->
            @Suppress("DEPRECATION")
            "$key=${formatValue(bundle.get(key))}"
        }
    } catch (e: Exception) {
        Timber.w(e, "Failed to read a nested bundle")
        bundle.toString()
    }

    private companion object {

        const val NULL = "null"
        const val MAX_ELEMENTS = 256
    }
}
