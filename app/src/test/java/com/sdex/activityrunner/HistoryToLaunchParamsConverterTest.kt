package com.sdex.activityrunner

import com.sdex.activityrunner.db.history.HistoryModel
import com.sdex.activityrunner.intent.converter.HistoryToLaunchParamsConverter
import org.junit.Assert.assertEquals
import org.junit.Test

class HistoryToLaunchParamsConverterTest {

    @Test
    fun testConvertEmpty() {
        val historyModel = HistoryModel(
            timestamp = 0L,
            name = null,
            packageName = null,
            className = null,
        )

        val converter = HistoryToLaunchParamsConverter(historyModel)
        val launchParams = converter.convert()

        assertEquals(launchParams.action, historyModel.action)
        assertEquals(launchParams.data, historyModel.data)
        assertEquals(launchParams.mimeType, historyModel.mimeType)
        assertEquals(launchParams.packageName, historyModel.packageName)
        assertEquals(launchParams.className, historyModel.className)
    }

    @Test
    fun testConvert() {
        val historyModel = HistoryModel(
            id = 0,
            timestamp = 0L,
            name = null,
            packageName = "pkg",
            className = "cls",
            action = "action",
            data = "data",
            mimeType = "type",
            categories = null,
            flags = null,
            extras = null,
        )

        val converter = HistoryToLaunchParamsConverter(historyModel)
        val launchParams = converter.convert()

        assertEquals(launchParams.action, historyModel.action)
        assertEquals(launchParams.data, historyModel.data)
        assertEquals(launchParams.mimeType, historyModel.mimeType)
        assertEquals(launchParams.packageName, historyModel.packageName)
        assertEquals(launchParams.className, historyModel.className)
    }
}
