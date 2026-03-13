package com.sdex.activityrunner

import com.sdex.activityrunner.intent.LaunchParams
import com.sdex.activityrunner.intent.LaunchParamsExtra
import com.sdex.activityrunner.intent.LaunchParamsExtraType
import com.sdex.activityrunner.intent.converter.ExtrasSerializer
import com.sdex.activityrunner.intent.converter.LaunchParamsToHistoryConverter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LaunchParamsToHistoryConverterTest {

    private val extrasSerializer = ExtrasSerializer()

    @Test
    fun `convert with all null values`() {
        val launchParams = LaunchParams()

        val converter = LaunchParamsToHistoryConverter(launchParams)
        val historyModel = converter.convert()

        assertEquals(null, historyModel.packageName)
        assertEquals(null, historyModel.className)
        assertEquals(null, historyModel.action)
        assertEquals(null, historyModel.data)
        assertEquals(null, historyModel.mimeType)
        assertEquals("", historyModel.categories)
        assertEquals("", historyModel.flags)
        assertEquals("", historyModel.extras)
        assertEquals(null, historyModel.name)
        assertNotNull(historyModel.timestamp)
    }

    @Test
    fun `convert with basic fields populated`() {
        val launchParams = LaunchParams().apply {
            packageName = "com.example.app"
            className = "com.example.app.MainActivity"
            action = "android.intent.action.MAIN"
            data = "https://example.com"
            mimeType = "text/plain"
        }

        val converter = LaunchParamsToHistoryConverter(launchParams)
        val historyModel = converter.convert()

        assertEquals("com.example.app", historyModel.packageName)
        assertEquals("com.example.app.MainActivity", historyModel.className)
        assertEquals("android.intent.action.MAIN", historyModel.action)
        assertEquals("https://example.com", historyModel.data)
        assertEquals("text/plain", historyModel.mimeType)
    }

    @Test
    fun `convert with categories serialized`() {
        val categories = arrayListOf(1, 2, 3)
        val launchParams = LaunchParams().apply {
            this.categories = categories
        }

        val converter = LaunchParamsToHistoryConverter(launchParams)
        val historyModel = converter.convert()

        assertEquals("1,2,3", historyModel.categories)
    }

    @Test
    fun `convert with empty categories`() {
        val launchParams = LaunchParams().apply {
            categories = arrayListOf()
        }

        val converter = LaunchParamsToHistoryConverter(launchParams)
        val historyModel = converter.convert()

        assertEquals("", historyModel.categories)
    }

    @Test
    fun `convert with flags serialized`() {
        val flags = arrayListOf(268435456, 67108864)
        val launchParams = LaunchParams().apply {
            this.flags = flags
        }

        val converter = LaunchParamsToHistoryConverter(launchParams)
        val historyModel = converter.convert()

        assertEquals("268435456,67108864", historyModel.flags)
    }

    @Test
    fun `convert with empty flags`() {
        val launchParams = LaunchParams().apply {
            flags = arrayListOf()
        }

        val converter = LaunchParamsToHistoryConverter(launchParams)
        val historyModel = converter.convert()

        assertEquals("", historyModel.flags)
    }

    @Test
    fun `convert with extras serialized`() {
        val extras = arrayListOf(
            LaunchParamsExtra("key1", "value1", LaunchParamsExtraType.STRING, false),
            LaunchParamsExtra("key2", "42", LaunchParamsExtraType.INT, false),
        )
        val launchParams = LaunchParams().apply {
            this.extras = extras
        }

        val converter = LaunchParamsToHistoryConverter(launchParams)
        val historyModel = converter.convert()

        val expectedSerialized = extrasSerializer.serialize(extras)
        assertEquals(expectedSerialized, historyModel.extras)
    }

    @Test
    fun `convert with empty extras`() {
        val launchParams = LaunchParams().apply {
            extras = arrayListOf()
        }

        val converter = LaunchParamsToHistoryConverter(launchParams)
        val historyModel = converter.convert()

        assertEquals("", historyModel.extras)
    }

    @Test
    fun `convert with all fields populated`() {
        val categories = arrayListOf(1, 2)
        val flags = arrayListOf(268435456)
        val extras = arrayListOf(
            LaunchParamsExtra("string_key", "string_value", LaunchParamsExtraType.STRING, false),
            LaunchParamsExtra("int_key", "123", LaunchParamsExtraType.INT, false),
            LaunchParamsExtra("bool_key", "true", LaunchParamsExtraType.BOOLEAN, false),
        )

        val launchParams = LaunchParams().apply {
            packageName = "com.test.package"
            className = "com.test.package.TestActivity"
            action = "android.intent.action.VIEW"
            data = "content://test/data"
            mimeType = "application/json"
            this.categories = categories
            this.flags = flags
            this.extras = extras
        }

        val converter = LaunchParamsToHistoryConverter(launchParams)
        val historyModel = converter.convert()

        assertEquals("com.test.package", historyModel.packageName)
        assertEquals("com.test.package.TestActivity", historyModel.className)
        assertEquals("android.intent.action.VIEW", historyModel.action)
        assertEquals("content://test/data", historyModel.data)
        assertEquals("application/json", historyModel.mimeType)
        assertEquals("1,2", historyModel.categories)
        assertEquals("268435456", historyModel.flags)
        assertEquals(extrasSerializer.serialize(extras), historyModel.extras)
        assertEquals(null, historyModel.name)
        assertNotNull(historyModel.timestamp)
    }

    @Test
    fun `convert sets current timestamp`() {
        val launchParams = LaunchParams()

        val beforeConvert = System.currentTimeMillis()
        val converter = LaunchParamsToHistoryConverter(launchParams)
        val historyModel = converter.convert()
        val afterConvert = System.currentTimeMillis()

        assertTrue(historyModel.timestamp in beforeConvert..afterConvert)
    }

    @Test
    fun `convert sets name to null`() {
        val launchParams = LaunchParams().apply {
            packageName = "test"
        }

        val converter = LaunchParamsToHistoryConverter(launchParams)
        val historyModel = converter.convert()

        assertEquals(null, historyModel.name)
    }

    @Test
    fun `convert with only package and class name minimal case`() {
        val launchParams = LaunchParams().apply {
            packageName = "com.minimal.app"
            className = "com.minimal.app.MainActivity"
        }

        val converter = LaunchParamsToHistoryConverter(launchParams)
        val historyModel = converter.convert()

        assertEquals("com.minimal.app", historyModel.packageName)
        assertEquals("com.minimal.app.MainActivity", historyModel.className)
        assertEquals(null, historyModel.action)
        assertEquals(null, historyModel.data)
        assertEquals(null, historyModel.mimeType)
        assertEquals("", historyModel.categories)
        assertEquals("", historyModel.flags)
        assertEquals("", historyModel.extras)
    }

    @Test
    fun `convert with array extras`() {
        val extras = arrayListOf(
            LaunchParamsExtra("int_array", "[1,2,3]", LaunchParamsExtraType.INT, true),
            LaunchParamsExtra("string_array", "[a,b,c]", LaunchParamsExtraType.STRING, true),
        )
        val launchParams = LaunchParams().apply {
            this.extras = extras
        }

        val converter = LaunchParamsToHistoryConverter(launchParams)
        val historyModel = converter.convert()

        val expectedSerialized = extrasSerializer.serialize(extras)
        assertEquals(expectedSerialized, historyModel.extras)
    }

    @Test
    fun `convert with single category and flag`() {
        val launchParams = LaunchParams().apply {
            categories = arrayListOf(1)
            flags = arrayListOf(268435456)
        }

        val converter = LaunchParamsToHistoryConverter(launchParams)
        val historyModel = converter.convert()

        assertEquals("1", historyModel.categories)
        assertEquals("268435456", historyModel.flags)
    }

    @Test
    fun `convert round-trip with HistoryToLaunchParamsConverter`() {
        val originalLaunchParams = LaunchParams().apply {
            packageName = "com.roundtrip.app"
            className = "com.roundtrip.app.Activity"
            action = "android.intent.action.SEND"
            data = "file:///test.txt"
            mimeType = "text/plain"
            categories = arrayListOf(1, 2)
            flags = arrayListOf(1, 2, 3)
            extras = arrayListOf(
                LaunchParamsExtra("key", "value", LaunchParamsExtraType.STRING, false),
            )
        }

        // Convert to HistoryModel
        val toHistoryConverter = LaunchParamsToHistoryConverter(originalLaunchParams)
        val historyModel = toHistoryConverter.convert()

        // Verify all fields were serialized correctly
        assertEquals("com.roundtrip.app", historyModel.packageName)
        assertEquals("com.roundtrip.app.Activity", historyModel.className)
        assertEquals("android.intent.action.SEND", historyModel.action)
        assertEquals("file:///test.txt", historyModel.data)
        assertEquals("text/plain", historyModel.mimeType)
        assertEquals("1,2", historyModel.categories)
        assertEquals("1,2,3", historyModel.flags)
        assertNotNull(historyModel.extras)
    }

    @Test
    fun `convert with negative integers in categories and flags`() {
        val launchParams = LaunchParams().apply {
            categories = arrayListOf(-1, 0, 1)
            flags = arrayListOf(-999, Int.MIN_VALUE)
        }

        val converter = LaunchParamsToHistoryConverter(launchParams)
        val historyModel = converter.convert()

        assertEquals("-1,0,1", historyModel.categories)
        assertEquals("-999,${Int.MIN_VALUE}", historyModel.flags)
    }

    @Test
    fun `convert with all extra types`() {
        val extras = arrayListOf(
            LaunchParamsExtra("str", "text", LaunchParamsExtraType.STRING, false),
            LaunchParamsExtra("int", "42", LaunchParamsExtraType.INT, false),
            LaunchParamsExtra("long", "9999999999", LaunchParamsExtraType.LONG, false),
            LaunchParamsExtra("float", "3.14", LaunchParamsExtraType.FLOAT, false),
            LaunchParamsExtra("double", "2.71828", LaunchParamsExtraType.DOUBLE, false),
            LaunchParamsExtra("bool", "false", LaunchParamsExtraType.BOOLEAN, false),
        )
        val launchParams = LaunchParams().apply {
            this.extras = extras
        }

        val converter = LaunchParamsToHistoryConverter(launchParams)
        val historyModel = converter.convert()

        val expectedSerialized = extrasSerializer.serialize(extras)
        assertEquals(expectedSerialized, historyModel.extras)
    }
}
