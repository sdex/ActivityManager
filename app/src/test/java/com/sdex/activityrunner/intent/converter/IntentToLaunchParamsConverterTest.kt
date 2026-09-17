package com.sdex.activityrunner.intent.converter

import android.content.ClipData
import android.content.ComponentName
import android.content.Intent
import androidx.core.net.toUri
import com.google.common.truth.Truth.assertThat
import com.sdex.activityrunner.intent.LaunchParams
import com.sdex.activityrunner.intent.LaunchParamsExtra
import com.sdex.activityrunner.intent.LaunchParamsExtraType
import com.sdex.activityrunner.intent.param.Category
import com.sdex.activityrunner.intent.param.Flag
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class IntentToLaunchParamsConverterTest {

    @Test
    fun `convert empty intent`() {
        val params = convert(Intent())

        assertThat(params.packageName).isNull()
        assertThat(params.className).isNull()
        assertThat(params.action).isNull()
        assertThat(params.data).isNull()
        assertThat(params.mimeType).isNull()
        assertThat(params.categories).isEmpty()
        assertThat(params.flags).isEmpty()
        assertThat(params.extras).isEmpty()
    }

    @Test
    fun `convert takes the component apart`() {
        val intent = Intent().apply {
            component = ComponentName("com.example.app", "com.example.app.MainActivity")
        }

        val params = convert(intent)

        assertThat(params.packageName).isEqualTo("com.example.app")
        assertThat(params.className).isEqualTo("com.example.app.MainActivity")
    }

    @Test
    fun `convert falls back to the package when there is no component`() {
        val intent = Intent().setPackage("com.example.app")

        val params = convert(intent)

        assertThat(params.packageName).isEqualTo("com.example.app")
        assertThat(params.className).isNull()
    }

    @Test
    fun `convert with action data and mime type`() {
        val intent = Intent(Intent.ACTION_SEND)
            .setDataAndType("https://example.com/path".toUri(), "text/plain")

        val params = convert(intent)

        assertThat(params.action).isEqualTo(Intent.ACTION_SEND)
        assertThat(params.data).isEqualTo("https://example.com/path")
        assertThat(params.mimeType).isEqualTo("text/plain")
    }

    @Test
    fun `convert with categories`() {
        val intent = Intent().apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addCategory(Intent.CATEGORY_BROWSABLE)
        }

        val params = convert(intent)

        assertThat(params.categories).containsExactly(
            Category.list().indexOf("CATEGORY_DEFAULT"),
            Category.list().indexOf("CATEGORY_BROWSABLE"),
        )
    }

    @Test
    fun `convert drops unknown categories`() {
        val intent = Intent().addCategory("com.example.CATEGORY_CUSTOM")

        assertThat(convert(intent).categories).isEmpty()
    }

    @Test
    fun `convert with flags`() {
        val intent = Intent().apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val params = convert(intent)

        // Activity and receiver flags share bits, so a raw mask maps onto every matching name.
        // The selection is redundant but adds up to the same value again, see the round trip below.
        assertThat(params.flags).containsAtLeast(
            Flag.list().indexOf("FLAG_ACTIVITY_NEW_TASK"),
            Flag.list().indexOf("FLAG_ACTIVITY_CLEAR_TOP"),
        )
        assertThat(LaunchParamsToIntentConverter(params).convert().flags).isEqualTo(intent.flags)
    }

    @Test
    fun `convert with scalar extras`() {
        val intent = Intent().apply {
            putExtra("str", "text")
            putExtra("int", 42)
            putExtra("long", 9999999999L)
            putExtra("float", 1.5f)
            putExtra("double", 2.5)
            putExtra("bool", true)
        }

        val extras = convert(intent).extras.associateBy { it.key }

        assertThat(extras.getValue("str"))
            .isEqualTo(LaunchParamsExtra("str", "text", LaunchParamsExtraType.STRING))
        assertThat(extras.getValue("int"))
            .isEqualTo(LaunchParamsExtra("int", "42", LaunchParamsExtraType.INT))
        assertThat(extras.getValue("long"))
            .isEqualTo(LaunchParamsExtra("long", "9999999999", LaunchParamsExtraType.LONG))
        assertThat(extras.getValue("float"))
            .isEqualTo(LaunchParamsExtra("float", "1.5", LaunchParamsExtraType.FLOAT))
        assertThat(extras.getValue("double"))
            .isEqualTo(LaunchParamsExtra("double", "2.5", LaunchParamsExtraType.DOUBLE))
        assertThat(extras.getValue("bool"))
            .isEqualTo(LaunchParamsExtra("bool", "true", LaunchParamsExtraType.BOOLEAN))
    }

    @Test
    fun `convert with array extras`() {
        val intent = Intent().apply {
            putExtra("strs", arrayOf("a", "b"))
            putExtra("ints", intArrayOf(1, 2))
            putExtra("bools", booleanArrayOf(true, false))
        }

        val extras = convert(intent).extras.associateBy { it.key }

        assertThat(extras.getValue("strs"))
            .isEqualTo(LaunchParamsExtra("strs", "a,b", LaunchParamsExtraType.STRING, true))
        assertThat(extras.getValue("ints"))
            .isEqualTo(LaunchParamsExtra("ints", "1,2", LaunchParamsExtraType.INT, true))
        assertThat(extras.getValue("bools"))
            .isEqualTo(LaunchParamsExtra("bools", "true,false", LaunchParamsExtraType.BOOLEAN, true))
    }

    @Test
    fun `convert keeps an array with a comma inside as a string`() {
        val intent = Intent().putExtra("strs", arrayOf("a,b", "c"))

        val extra = convert(intent).extras.single()

        assertThat(extra.isArray).isFalse()
        assertThat(extra.type).isEqualTo(LaunchParamsExtraType.STRING)
        assertThat(extra.value).isEqualTo("[a,b, c]")
    }

    @Test
    fun `convert falls back to the string form of an unsupported extra`() {
        val intent = Intent().putExtra("uri", "content://media/1".toUri())

        val extra = convert(intent).extras.single()

        assertThat(extra.type).isEqualTo(LaunchParamsExtraType.STRING)
        assertThat(extra.value).isEqualTo("content://media/1")
    }

    @Test
    fun `converting back and forth keeps the intent`() {
        val original = Intent(Intent.ACTION_VIEW).apply {
            component = ComponentName("com.example.app", "com.example.app.MainActivity")
            setDataAndType("https://example.com".toUri(), "text/plain")
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("str", "text")
            putExtra("ints", intArrayOf(1, 2))
        }

        val restored = LaunchParamsToIntentConverter(convert(original)).convert()

        assertThat(restored.action).isEqualTo(Intent.ACTION_VIEW)
        assertThat(restored.component).isEqualTo(original.component)
        assertThat(restored.dataString).isEqualTo("https://example.com")
        assertThat(restored.type).isEqualTo("text/plain")
        assertThat(restored.categories).containsExactly(Intent.CATEGORY_DEFAULT)
        assertThat(restored.flags).isEqualTo(Intent.FLAG_ACTIVITY_NEW_TASK)
        assertThat(restored.getStringExtra("str")).isEqualTo("text")
        assertThat(restored.getIntArrayExtra("ints")).isEqualTo(intArrayOf(1, 2))
    }

    @Test
    fun `a data uri is passed on with its grant flags`() {
        val intent = Intent(Intent.ACTION_VIEW, "content://media/external/images/1".toUri())
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        assertThat(IntentToLaunchParamsConverter.getUnsupportedUris(intent)).isEmpty()
        val restored = LaunchParamsToIntentConverter(convert(intent)).convert()
        assertThat(restored.data).isEqualTo("content://media/external/images/1".toUri())
        assertThat(restored.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION).isNotEqualTo(0)
    }

    @Test
    fun `a uri extra is reported as unsupported`() {
        val uri = "content://media/external/images/1".toUri()
        val intent = Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_STREAM, uri)

        assertThat(IntentToLaunchParamsConverter.getUnsupportedUris(intent)).containsExactly(uri)
    }

    @Test
    fun `a list of uri extras is reported as unsupported`() {
        val first = "content://media/external/images/1".toUri()
        val second = "file:///storage/emulated/0/a.png".toUri()
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
            .putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayListOf(first, second))

        assertThat(IntentToLaunchParamsConverter.getUnsupportedUris(intent))
            .containsExactly(first, second)
    }

    @Test
    fun `clip data uris are reported as unsupported`() {
        val uri = "content://media/external/images/1".toUri()
        val intent = Intent(Intent.ACTION_SEND).apply {
            clipData = ClipData.newRawUri("label", uri)
        }

        assertThat(IntentToLaunchParamsConverter.getUnsupportedUris(intent)).containsExactly(uri)
    }

    @Test
    fun `the same uri is only reported once`() {
        val uri = "content://media/external/images/1".toUri()
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, uri)
            clipData = ClipData.newRawUri("label", uri)
        }

        assertThat(IntentToLaunchParamsConverter.getUnsupportedUris(intent)).containsExactly(uri)
    }

    @Test
    fun `a web uri extra needs no grant and is not reported`() {
        val intent = Intent(Intent.ACTION_SEND)
            .putExtra(Intent.EXTRA_STREAM, "https://example.com/a.png".toUri())

        assertThat(IntentToLaunchParamsConverter.getUnsupportedUris(intent)).isEmpty()
    }

    @Test
    fun `a plain text share is not reported`() {
        val intent = Intent(Intent.ACTION_SEND)
            .setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, "text")

        assertThat(IntentToLaunchParamsConverter.getUnsupportedUris(intent)).isEmpty()
    }

    private fun convert(intent: Intent): LaunchParams =
        IntentToLaunchParamsConverter(intent).convert()
}
