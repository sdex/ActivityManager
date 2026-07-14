package com.sdex.activityrunner

import android.content.Intent
import com.sdex.activityrunner.intent.LaunchParams
import com.sdex.activityrunner.intent.LaunchParamsExtra
import com.sdex.activityrunner.intent.LaunchParamsExtraType
import com.sdex.activityrunner.intent.converter.LaunchParamsToIntentConverter
import com.sdex.activityrunner.intent.param.Category
import com.sdex.activityrunner.intent.param.Flag
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LaunchParamsToIntentConverterTest {

    @Test
    fun `convert with empty params`() {
        val intent = convert(LaunchParams())

        assertEquals(Intent.ACTION_MAIN, intent.action)
        assertNull(intent.component)
        assertNull(intent.`package`)
        assertNull(intent.data)
        assertNull(intent.type)
        assertNull(intent.categories)
        assertEquals(0, intent.flags)
        assertNull(intent.extras)
    }

    @Test
    fun `convert with component`() {
        val intent = convert(
            LaunchParams(
                packageName = "com.example.app",
                className = "com.example.app.MainActivity",
            ),
        )

        assertEquals("com.example.app", intent.`package`)
        assertEquals("com.example.app", intent.component?.packageName)
        assertEquals("com.example.app.MainActivity", intent.component?.className)
    }

    @Test
    fun `convert with package only`() {
        val intent = convert(LaunchParams(packageName = "com.example.app"))

        assertEquals("com.example.app", intent.`package`)
        assertNull(intent.component)
    }

    @Test
    fun `convert with action`() {
        val intent = convert(LaunchParams(action = "android.intent.action.VIEW"))

        assertEquals("android.intent.action.VIEW", intent.action)
    }

    @Test
    fun `convert with data and mime type`() {
        val intent = convert(
            LaunchParams(
                data = "https://example.com/path",
                mimeType = "text/plain",
            ),
        )

        assertEquals("https://example.com/path", intent.dataString)
        assertEquals("text/plain", intent.type)
    }

    @Test
    fun `convert ignores mime type without data`() {
        val intent = convert(LaunchParams(mimeType = "text/plain"))

        assertNull(intent.data)
        assertNull(intent.type)
    }

    @Test
    fun `convert with categories`() {
        val intent = convert(
            LaunchParams(categories = listOf(Category.list().indexOf("CATEGORY_DEFAULT"))),
        )

        assertEquals(setOf(Intent.CATEGORY_DEFAULT), intent.categories)
    }

    @Test
    fun `convert with flags`() {
        val intent = convert(
            LaunchParams(
                flags = listOf(
                    Flag.list().indexOf("FLAG_ACTIVITY_NEW_TASK"),
                    Flag.list().indexOf("FLAG_ACTIVITY_CLEAR_TOP"),
                ),
            ),
        )

        assertEquals(
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP,
            intent.flags,
        )
    }

    @Test
    fun `convert with all scalar extra types`() {
        val intent = convert(
            LaunchParams(
                extras = listOf(
                    LaunchParamsExtra("str", "text", LaunchParamsExtraType.STRING),
                    LaunchParamsExtra("int", "42", LaunchParamsExtraType.INT),
                    LaunchParamsExtra("long", "9999999999", LaunchParamsExtraType.LONG),
                    LaunchParamsExtra("float", "3.14", LaunchParamsExtraType.FLOAT),
                    LaunchParamsExtra("double", "2.71828", LaunchParamsExtraType.DOUBLE),
                    LaunchParamsExtra("bool", "true", LaunchParamsExtraType.BOOLEAN),
                ),
            ),
        )

        assertEquals("text", intent.getStringExtra("str"))
        assertEquals(42, intent.getIntExtra("int", 0))
        assertEquals(9999999999L, intent.getLongExtra("long", 0))
        assertEquals(3.14f, intent.getFloatExtra("float", 0f), 0f)
        assertEquals(2.71828, intent.getDoubleExtra("double", 0.0), 0.0)
        assertTrue(intent.getBooleanExtra("bool", false))
    }

    @Test
    fun `convert with all array extra types`() {
        val intent = convert(
            LaunchParams(
                extras = listOf(
                    LaunchParamsExtra("strs", "a,b,c", LaunchParamsExtraType.STRING, true),
                    LaunchParamsExtra("ints", "1,2,3", LaunchParamsExtraType.INT, true),
                    LaunchParamsExtra("longs", "1,9999999999", LaunchParamsExtraType.LONG, true),
                    LaunchParamsExtra("floats", "1.5,2.5", LaunchParamsExtraType.FLOAT, true),
                    LaunchParamsExtra("doubles", "1.5,2.5", LaunchParamsExtraType.DOUBLE, true),
                    LaunchParamsExtra("bools", "true,false", LaunchParamsExtraType.BOOLEAN, true),
                ),
            ),
        )

        assertArrayEquals(arrayOf("a", "b", "c"), intent.getStringArrayExtra("strs"))
        assertArrayEquals(intArrayOf(1, 2, 3), intent.getIntArrayExtra("ints"))
        assertArrayEquals(longArrayOf(1, 9999999999L), intent.getLongArrayExtra("longs"))
        assertArrayEquals(floatArrayOf(1.5f, 2.5f), intent.getFloatArrayExtra("floats"), 0f)
        assertArrayEquals(doubleArrayOf(1.5, 2.5), intent.getDoubleArrayExtra("doubles"), 0.0)
        assertArrayEquals(
            booleanArrayOf(true, false),
            intent.getBooleanArrayExtra("bools"),
        )
    }

    @Test
    fun `convert drops invalid scalar extra`() {
        val intent = convert(
            LaunchParams(
                extras = listOf(
                    LaunchParamsExtra("int", "not a number", LaunchParamsExtraType.INT),
                ),
            ),
        )

        assertFalse(intent.hasExtra("int"))
    }

    @Test
    fun `convert drops array extra with invalid element`() {
        val intent = convert(
            LaunchParams(
                extras = listOf(
                    LaunchParamsExtra("ints", "1,two,3", LaunchParamsExtraType.INT, true),
                    LaunchParamsExtra("bools", "true,no", LaunchParamsExtraType.BOOLEAN, true),
                ),
            ),
        )

        assertFalse(intent.hasExtra("ints"))
        assertFalse(intent.hasExtra("bools"))
    }

    private fun convert(launchParams: LaunchParams): Intent =
        LaunchParamsToIntentConverter(launchParams).convert()
}
