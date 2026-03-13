package com.sdex.activityrunner

import com.sdex.activityrunner.db.history.HistoryModel
import com.sdex.activityrunner.intent.LaunchParamsExtra
import com.sdex.activityrunner.intent.LaunchParamsExtraType
import com.sdex.activityrunner.intent.converter.ExtrasSerializer
import com.sdex.activityrunner.intent.converter.HistoryToLaunchParamsConverter
import com.sdex.activityrunner.intent.converter.IntegerListSerializer
import org.junit.Assert.assertEquals
import org.junit.Test

class HistoryToLaunchParamsConverterTest {

    private val integerListSerializer = IntegerListSerializer()
    private val extrasSerializer = ExtrasSerializer()

    @Test
    fun `convert with all null values`() {
        val historyModel = HistoryModel(
            id = 0,
            timestamp = 0L,
            name = null,
            packageName = null,
            className = null,
            action = null,
            data = null,
            mimeType = null,
            categories = null,
            flags = null,
            extras = null,
        )

        val converter = HistoryToLaunchParamsConverter(historyModel)
        val launchParams = converter.convert()

        assertEquals(null, launchParams.packageName)
        assertEquals(null, launchParams.className)
        assertEquals(null, launchParams.action)
        assertEquals(null, launchParams.data)
        assertEquals(null, launchParams.mimeType)
        assertEquals(emptyList<Int>(), launchParams.categories)
        assertEquals(emptyList<Int>(), launchParams.flags)
        assertEquals(emptyList<LaunchParamsExtra>(), launchParams.extras)
    }

    @Test
    fun `convert with basic fields populated`() {
        val historyModel = HistoryModel(
            id = 1,
            timestamp = 1234567890L,
            name = "Test Activity",
            packageName = "com.example.app",
            className = "com.example.app.MainActivity",
            action = "android.intent.action.MAIN",
            data = "https://example.com",
            mimeType = "text/plain",
            categories = null,
            flags = null,
            extras = null,
        )

        val converter = HistoryToLaunchParamsConverter(historyModel)
        val launchParams = converter.convert()

        assertEquals("com.example.app", launchParams.packageName)
        assertEquals("com.example.app.MainActivity", launchParams.className)
        assertEquals("android.intent.action.MAIN", launchParams.action)
        assertEquals("https://example.com", launchParams.data)
        assertEquals("text/plain", launchParams.mimeType)
    }

    @Test
    fun `convert with categories deserialized`() {
        val categories = listOf(1, 2, 3)
        val historyModel = HistoryModel(
            id = 0,
            timestamp = 0L,
            name = null,
            packageName = null,
            className = null,
            categories = integerListSerializer.serialize(ArrayList(categories)),
            flags = null,
            extras = null,
        )

        val converter = HistoryToLaunchParamsConverter(historyModel)
        val launchParams = converter.convert()

        assertEquals(categories, launchParams.categories)
    }

    @Test
    fun `convert with empty categories string`() {
        val historyModel = HistoryModel(
            id = 0,
            timestamp = 0L,
            name = null,
            packageName = null,
            className = null,
            categories = "",
            flags = null,
            extras = null,
        )

        val converter = HistoryToLaunchParamsConverter(historyModel)
        val launchParams = converter.convert()

        assertEquals(emptyList<Int>(), launchParams.categories)
    }

    @Test
    fun `convert with flags deserialized`() {
        val flags = listOf(268435456, 67108864)
        val historyModel = HistoryModel(
            id = 0,
            timestamp = 0L,
            name = null,
            packageName = null,
            className = null,
            categories = null,
            flags = integerListSerializer.serialize(ArrayList(flags)),
            extras = null,
        )

        val converter = HistoryToLaunchParamsConverter(historyModel)
        val launchParams = converter.convert()

        assertEquals(flags, launchParams.flags)
    }

    @Test
    fun `convert with empty flags string`() {
        val historyModel = HistoryModel(
            id = 0,
            timestamp = 0L,
            name = null,
            packageName = null,
            className = null,
            categories = null,
            flags = "",
            extras = null,
        )

        val converter = HistoryToLaunchParamsConverter(historyModel)
        val launchParams = converter.convert()

        assertEquals(emptyList<Int>(), launchParams.flags)
    }

    @Test
    fun `convert with extras deserialized`() {
        val extras = listOf(
            LaunchParamsExtra("key1", "value1", LaunchParamsExtraType.STRING, false),
            LaunchParamsExtra("key2", "42", LaunchParamsExtraType.INT, false),
        )
        val historyModel = HistoryModel(
            id = 0,
            timestamp = 0L,
            name = null,
            packageName = null,
            className = null,
            categories = null,
            flags = null,
            extras = extrasSerializer.serialize(extras),
        )

        val converter = HistoryToLaunchParamsConverter(historyModel)
        val launchParams = converter.convert()

        assertEquals(extras, launchParams.extras)
    }

    @Test
    fun `convert with empty extras string`() {
        val historyModel = HistoryModel(
            id = 0,
            timestamp = 0L,
            name = null,
            packageName = null,
            className = null,
            categories = null,
            flags = null,
            extras = "",
        )

        val converter = HistoryToLaunchParamsConverter(historyModel)
        val launchParams = converter.convert()

        assertEquals(emptyList<LaunchParamsExtra>(), launchParams.extras)
    }

    @Test
    fun `convert with all fields populated`() {
        val categories = listOf(1, 2)
        val flags = listOf(268435456)
        val extras = listOf(
            LaunchParamsExtra("string_key", "string_value", LaunchParamsExtraType.STRING, false),
            LaunchParamsExtra("int_key", "123", LaunchParamsExtraType.INT, false),
            LaunchParamsExtra("bool_key", "true", LaunchParamsExtraType.BOOLEAN, false),
        )

        val historyModel = HistoryModel(
            id = 42,
            timestamp = 9876543210L,
            name = "Full Test",
            packageName = "com.test.package",
            className = "com.test.package.TestActivity",
            action = "android.intent.action.VIEW",
            data = "content://test/data",
            mimeType = "application/json",
            categories = integerListSerializer.serialize(ArrayList(categories)),
            flags = integerListSerializer.serialize(ArrayList(flags)),
            extras = extrasSerializer.serialize(extras),
        )

        val converter = HistoryToLaunchParamsConverter(historyModel)
        val launchParams = converter.convert()

        assertEquals("com.test.package", launchParams.packageName)
        assertEquals("com.test.package.TestActivity", launchParams.className)
        assertEquals("android.intent.action.VIEW", launchParams.action)
        assertEquals("content://test/data", launchParams.data)
        assertEquals("application/json", launchParams.mimeType)
        assertEquals(categories, launchParams.categories)
        assertEquals(flags, launchParams.flags)
        assertEquals(extras, launchParams.extras)
    }

    @Test
    fun `convert preserves id and name but they are not used in LaunchParams`() {
        val historyModel = HistoryModel(
            id = 999,
            timestamp = 1111111111L,
            name = "Unused Name",
            packageName = "pkg",
            className = "cls",
            action = null,
            data = null,
            mimeType = null,
            categories = null,
            flags = null,
            extras = null,
        )

        val converter = HistoryToLaunchParamsConverter(historyModel)
        val launchParams = converter.convert()

        // These fields exist in HistoryModel but are not transferred to LaunchParams
        assertEquals("pkg", launchParams.packageName)
        assertEquals("cls", launchParams.className)
        // id and name are effectively ignored by the converter
    }

    @Test
    fun `convert with only package and class name minimal case`() {
        val historyModel = HistoryModel(
            id = 0,
            timestamp = 0L,
            name = null,
            packageName = "com.minimal.app",
            className = "com.minimal.app.MainActivity",
        )

        val converter = HistoryToLaunchParamsConverter(historyModel)
        val launchParams = converter.convert()

        assertEquals("com.minimal.app", launchParams.packageName)
        assertEquals("com.minimal.app.MainActivity", launchParams.className)
        assertEquals(null, launchParams.action)
        assertEquals(null, launchParams.data)
        assertEquals(null, launchParams.mimeType)
        assertEquals(emptyList<Int>(), launchParams.categories)
        assertEquals(emptyList<Int>(), launchParams.flags)
        assertEquals(emptyList<LaunchParamsExtra>(), launchParams.extras)
    }

    @Test
    fun `convert with array extras`() {
        val extras = listOf(
            LaunchParamsExtra("int_array", "[1,2,3]", LaunchParamsExtraType.INT, true),
            LaunchParamsExtra("string_array", "[a,b,c]", LaunchParamsExtraType.STRING, true),
        )
        val historyModel = HistoryModel(
            id = 0,
            timestamp = 0L,
            name = null,
            packageName = null,
            className = null,
            categories = null,
            flags = null,
            extras = extrasSerializer.serialize(extras),
        )

        val converter = HistoryToLaunchParamsConverter(historyModel)
        val launchParams = converter.convert()

        assertEquals(extras, launchParams.extras)
    }

    @Test
    fun `convert with single category and flag`() {
        val historyModel = HistoryModel(
            id = 0,
            timestamp = 0L,
            name = null,
            packageName = null,
            className = null,
            categories = "1",
            flags = "268435456",
            extras = null,
        )

        val converter = HistoryToLaunchParamsConverter(historyModel)
        val launchParams = converter.convert()

        assertEquals(listOf(1), launchParams.categories)
        assertEquals(listOf(268435456), launchParams.flags)
    }
}
