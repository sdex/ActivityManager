package com.sdex.activityrunner

import com.sdex.activityrunner.intent.LaunchParams
import com.sdex.activityrunner.intent.converter.LaunchParamsToHistoryConverter
import org.junit.Assert.assertEquals
import org.junit.Test

class LaunchParamsToHistoryConverterTest {

    @Test
    fun testConvertEmpty() {
        val launchParams = LaunchParams()

        val converter = LaunchParamsToHistoryConverter(launchParams)
        val historyModel = converter.convert()

        assertEquals(launchParams.action, historyModel.action)
        assertEquals(launchParams.data, historyModel.data)
        assertEquals(launchParams.mimeType, historyModel.mimeType)
        assertEquals(launchParams.packageName, historyModel.packageName)
        assertEquals(launchParams.className, historyModel.className)
    }

    @Test
    fun testConvert() {
        val launchParams = LaunchParams()
        launchParams.action = "action"
        launchParams.data = "data"
        launchParams.mimeType = "type"
        launchParams.packageName = "pkg"
        launchParams.className = "cls"

        val converter = LaunchParamsToHistoryConverter(launchParams)
        val historyModel = converter.convert()

        assertEquals(launchParams.action, historyModel.action)
        assertEquals(launchParams.data, historyModel.data)
        assertEquals(launchParams.mimeType, historyModel.mimeType)
        assertEquals(launchParams.packageName, historyModel.packageName)
        assertEquals(launchParams.className, historyModel.className)
    }
}
