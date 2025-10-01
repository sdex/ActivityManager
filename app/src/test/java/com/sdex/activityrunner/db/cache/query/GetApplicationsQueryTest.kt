package com.sdex.activityrunner.db.cache.query

import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.preferences.AppPreferences
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class GetApplicationsQueryTest {

    private val appPreferences: AppPreferences = mockk()

    @Test
    fun `default query`() {
        every { appPreferences.isShowDisabledApps } returns true
        every { appPreferences.isShowSystemApps } returns true
        every { appPreferences.sortBy } returns ApplicationModel.NAME
        every { appPreferences.sortOrder } returns GetApplicationsQuery.ASC

        val query = GetApplicationsQuery(appPreferences)
        val expected =
            "SELECT * FROM ApplicationModel WHERE activitiesCount>0 ORDER BY name COLLATE NOCASE ASC"
        assertEquals(expected, query.toString().trim())
    }

    @Test
    fun `query without disabled apps`() {
        every { appPreferences.isShowDisabledApps } returns false
        every { appPreferences.isShowSystemApps } returns true
        every { appPreferences.sortBy } returns ApplicationModel.NAME
        every { appPreferences.sortOrder } returns GetApplicationsQuery.ASC

        val query = GetApplicationsQuery(appPreferences)
        val expected =
            "SELECT * FROM ApplicationModel WHERE activitiesCount>0 AND (enabled=1) ORDER BY name COLLATE NOCASE ASC"
        assertEquals(expected, query.toString().trim())
    }

    @Test
    fun `query without system apps`() {
        every { appPreferences.isShowDisabledApps } returns true
        every { appPreferences.isShowSystemApps } returns false
        every { appPreferences.sortBy } returns ApplicationModel.NAME
        every { appPreferences.sortOrder } returns GetApplicationsQuery.ASC

        val query = GetApplicationsQuery(appPreferences)
        val expected =
            "SELECT * FROM ApplicationModel WHERE activitiesCount>0 AND (system=0) ORDER BY name COLLATE NOCASE ASC"
        assertEquals(expected, query.toString().trim())
    }

    @Test
    fun `query without disabled and system apps`() {
        every { appPreferences.isShowDisabledApps } returns false
        every { appPreferences.isShowSystemApps } returns false
        every { appPreferences.sortBy } returns ApplicationModel.NAME
        every { appPreferences.sortOrder } returns GetApplicationsQuery.ASC

        val query = GetApplicationsQuery(appPreferences)
        val expected =
            "SELECT * FROM ApplicationModel WHERE activitiesCount>0 AND (enabled=1) AND (system=0) ORDER BY name COLLATE NOCASE ASC"
        assertEquals(expected, query.toString().trim())
    }

    @Test
    fun `query with search text`() {
        every { appPreferences.isShowDisabledApps } returns true
        every { appPreferences.isShowSystemApps } returns true
        every { appPreferences.sortBy } returns ApplicationModel.NAME
        every { appPreferences.sortOrder } returns GetApplicationsQuery.ASC

        val query = GetApplicationsQuery(appPreferences, "test")
        val expected =
            "SELECT * FROM ApplicationModel WHERE activitiesCount>0 AND (name LIKE '%test%' OR packageName LIKE '%test%') ORDER BY name COLLATE NOCASE ASC"
        assertEquals(expected, query.toString().trim())
    }

    @Test
    fun `query with search text and no disabled apps`() {
        every { appPreferences.isShowDisabledApps } returns false
        every { appPreferences.isShowSystemApps } returns true
        every { appPreferences.sortBy } returns ApplicationModel.NAME
        every { appPreferences.sortOrder } returns GetApplicationsQuery.ASC

        val query = GetApplicationsQuery(appPreferences, "test")
        val expected =
            "SELECT * FROM ApplicationModel WHERE activitiesCount>0 AND (enabled=1) AND (name LIKE '%test%' OR packageName LIKE '%test%') ORDER BY name COLLATE NOCASE ASC"
        assertEquals(expected, query.toString().trim())
    }

    @Test
    fun `query with search text and no system apps`() {
        every { appPreferences.isShowDisabledApps } returns true
        every { appPreferences.isShowSystemApps } returns false
        every { appPreferences.sortBy } returns ApplicationModel.NAME
        every { appPreferences.sortOrder } returns GetApplicationsQuery.ASC

        val query = GetApplicationsQuery(appPreferences, "test")
        val expected =
            "SELECT * FROM ApplicationModel WHERE activitiesCount>0 AND (system=0) AND (name LIKE '%test%' OR packageName LIKE '%test%') ORDER BY name COLLATE NOCASE ASC"
        assertEquals(expected, query.toString().trim())
    }

    @Test
    fun `query with search text and no disabled and no system apps`() {
        every { appPreferences.isShowDisabledApps } returns false
        every { appPreferences.isShowSystemApps } returns false
        every { appPreferences.sortBy } returns ApplicationModel.NAME
        every { appPreferences.sortOrder } returns GetApplicationsQuery.ASC

        val query = GetApplicationsQuery(appPreferences, "test")
        val expected =
            "SELECT * FROM ApplicationModel WHERE activitiesCount>0 AND (enabled=1) AND (system=0) AND (name LIKE '%test%' OR packageName LIKE '%test%') ORDER BY name COLLATE NOCASE ASC"
        assertEquals(expected, query.toString().trim())
    }

    @Test
    fun `query with different sort order`() {
        every { appPreferences.isShowDisabledApps } returns true
        every { appPreferences.isShowSystemApps } returns true
        every { appPreferences.sortBy } returns "packageName"
        every { appPreferences.sortOrder } returns GetApplicationsQuery.DESC

        val query = GetApplicationsQuery(appPreferences)
        val expected =
            "SELECT * FROM ApplicationModel WHERE activitiesCount>0 ORDER BY packageName COLLATE NOCASE DESC"
        assertEquals(expected, query.toString().trim())
    }

    @Test
    fun `query with search text containing single quote`() {
        every { appPreferences.isShowDisabledApps } returns true
        every { appPreferences.isShowSystemApps } returns true
        every { appPreferences.sortBy } returns ApplicationModel.NAME
        every { appPreferences.sortOrder } returns GetApplicationsQuery.ASC

        val query = GetApplicationsQuery(appPreferences, "test's")
        val expected =
            "SELECT * FROM ApplicationModel WHERE activitiesCount>0 AND (name LIKE '%test''s%' OR packageName LIKE '%test''s%') ORDER BY name COLLATE NOCASE ASC"
        assertEquals(expected, query.toString().trim())
    }
}
