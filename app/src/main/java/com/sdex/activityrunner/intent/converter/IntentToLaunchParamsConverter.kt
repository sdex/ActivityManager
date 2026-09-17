package com.sdex.activityrunner.intent.converter

import android.content.ContentResolver.SCHEME_CONTENT
import android.content.ContentResolver.SCHEME_FILE
import android.content.Intent
import android.net.Uri
import com.sdex.activityrunner.intent.LaunchParams
import com.sdex.activityrunner.intent.LaunchParamsExtra
import com.sdex.activityrunner.intent.LaunchParamsExtraType
import com.sdex.activityrunner.intent.param.Category
import com.sdex.activityrunner.intent.param.Flag
import timber.log.Timber

/**
 * Turns a real [Intent] into the editable form used by the intent launcher, so an intercepted
 * intent can be tweaked and sent further.
 *
 * The conversion is lossy by design: categories and flags this build does not know about are
 * dropped, and extras that the launcher cannot represent fall back to their string form.
 */
class IntentToLaunchParamsConverter(
    private val intent: Intent,
) : Converter<LaunchParams> {

    override fun convert(): LaunchParams {
        val component = intent.component
        return LaunchParams(
            packageName = component?.packageName ?: intent.`package`,
            className = component?.className,
            action = intent.action,
            data = intent.dataString,
            mimeType = intent.type,
            categories = Category.positions(intent.categories.orEmpty()),
            flags = Flag.positions(intent.flags),
            extras = convertExtras(),
        )
    }

    private fun convertExtras(): List<LaunchParamsExtra> {
        val extras = intent.extras ?: return emptyList()
        // Unparcelling fails when an extra references a class only the sending application has.
        val keys = try {
            extras.keySet()
        } catch (e: Exception) {
            Timber.w(e, "Failed to read the extras")
            return emptyList()
        }
        return keys.mapNotNull { key ->
            @Suppress("DEPRECATION")
            val value = runCatching { extras.get(key) }.getOrNull()
            convertExtra(key, value)
        }
    }

    private fun convertExtra(key: String, value: Any?): LaunchParamsExtra? = when (value) {
        null -> null
        is String -> extra(key, value, LaunchParamsExtraType.STRING)
        is Boolean -> extra(key, value.toString(), LaunchParamsExtraType.BOOLEAN)
        is Int -> extra(key, value.toString(), LaunchParamsExtraType.INT)
        is Long -> extra(key, value.toString(), LaunchParamsExtraType.LONG)
        is Float -> extra(key, value.toString(), LaunchParamsExtraType.FLOAT)
        is Double -> extra(key, value.toString(), LaunchParamsExtraType.DOUBLE)
        is BooleanArray -> array(key, value.toList(), LaunchParamsExtraType.BOOLEAN)
        is IntArray -> array(key, value.toList(), LaunchParamsExtraType.INT)
        is LongArray -> array(key, value.toList(), LaunchParamsExtraType.LONG)
        is FloatArray -> array(key, value.toList(), LaunchParamsExtraType.FLOAT)
        is DoubleArray -> array(key, value.toList(), LaunchParamsExtraType.DOUBLE)
        is Array<*> -> array(key, value.toList(), LaunchParamsExtraType.STRING)
        else -> extra(key, value.toString(), LaunchParamsExtraType.STRING)
    }

    private fun extra(key: String, value: String, type: Int) = LaunchParamsExtra(key, value, type)

    /**
     * Array extras are stored as a comma separated string, so a value that already contains a comma
     * would come back as two elements. Such arrays are kept as a plain string instead.
     */
    private fun array(key: String, values: List<Any?>, type: Int): LaunchParamsExtra {
        val elements = values.map { it?.toString().orEmpty() }
        return if (elements.any { it.contains(',') }) {
            LaunchParamsExtra(key, elements.joinToString(", ", "[", "]"), LaunchParamsExtraType.STRING)
        } else {
            LaunchParamsExtra(key, elements.joinToString(","), type, isArray = true)
        }
    }

    companion object {

        /**
         * Content and file uris that survive [convert] in name only.
         *
         * A uri in [Intent.getData] is fine: it round trips as the data string, the grant flags
         * round trip with it, and an application holding a grant may pass it on. Uris carried in
         * the clip data or in an extra do not - [LaunchParams] has no clip data at all, and a uri
         * extra is flattened into a plain string, so the target application gets text where it
         * expects a stream and no grant is issued for it.
         */
        fun getUnsupportedUris(intent: Intent): List<Uri> =
            (clipDataUris(intent) + extraUris(intent))
                .filter { it.scheme == SCHEME_CONTENT || it.scheme == SCHEME_FILE }
                .distinct()

        private fun clipDataUris(intent: Intent): List<Uri> {
            val clipData = intent.clipData ?: return emptyList()
            return (0 until clipData.itemCount).mapNotNull { index ->
                runCatching { clipData.getItemAt(index).uri }.getOrNull()
            }
        }

        private fun extraUris(intent: Intent): List<Uri> {
            val extras = intent.extras ?: return emptyList()
            val keys = try {
                extras.keySet()
            } catch (e: Exception) {
                Timber.w(e, "Failed to read the extras")
                return emptyList()
            }
            return keys.flatMap { key ->
                @Suppress("DEPRECATION")
                when (val value = runCatching { extras.get(key) }.getOrNull()) {
                    is Uri -> listOf(value)
                    is Array<*> -> value.filterIsInstance<Uri>()
                    is Iterable<*> -> value.filterIsInstance<Uri>()
                    else -> emptyList()
                }
            }
        }
    }
}
