package com.sdex.activityrunner.db.cache.query

import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.preferences.DisplayConfig
import org.junit.Assert.assertEquals
import org.junit.Test

class GetApplicationsQueryTest {

    @Test
    fun `default query`() {
        val displayConfig = DisplayConfig(
            showDisabledApps = true,
            showSystemApps = true,
            sortBy = ApplicationModel.NAME,
            sortOrder = GetApplicationsQuery.ASC
        )

        val query = GetApplicationsQuery(displayConfig)
        val expected =
            "SELECT * FROM ApplicationModel WHERE activitiesCount>0 ORDER BY name COLLATE NOCASE ASC"
        assertEquals(expected, query.toString().trim())
    }

    @Test
    fun `query without disabled apps`() {
        val displayConfig = DisplayConfig(
            showDisabledApps = false,
            showSystemApps = true,
            sortBy = ApplicationModel.NAME,
            sortOrder = GetApplicationsQuery.ASC
        )

        val query = GetApplicationsQuery(displayConfig)
        val expected =
            "SELECT * FROM ApplicationModel WHERE activitiesCount>0 AND (enabled=1) ORDER BY name COLLATE NOCASE ASC"
        assertEquals(expected, query.toString().trim())
    }

    @Test
    fun `query without system apps`() {
        val displayConfig = DisplayConfig(
            showDisabledApps = true,
            showSystemApps = false,
            sortBy = ApplicationModel.NAME,
            sortOrder = GetApplicationsQuery.ASC
        )

        val query = GetApplicationsQuery(displayConfig)
        val expected =
            "SELECT * FROM ApplicationModel WHERE activitiesCount>0 AND (system=0) ORDER BY name COLLATE NOCASE ASC"
        assertEquals(expected, query.toString().trim())
    }

    @Test
    fun `query without disabled and system apps`() {
        val displayConfig = DisplayConfig(
            showDisabledApps = false,
            showSystemApps = false,
            sortBy = ApplicationModel.NAME,
            sortOrder = GetApplicationsQuery.ASC
        )

        val query = GetApplicationsQuery(displayConfig)
        val expected =
            "SELECT * FROM ApplicationModel WHERE activitiesCount>0 AND (enabled=1) AND (system=0) ORDER BY name COLLATE NOCASE ASC"
        assertEquals(expected, query.toString().trim())
    }

    @Test
    fun `query with search text`() {
        val displayConfig = DisplayConfig(
            showDisabledApps = true,
            showSystemApps = true,
            sortBy = ApplicationModel.NAME,
            sortOrder = GetApplicationsQuery.ASC
        )

        val query = GetApplicationsQuery(displayConfig, "test")
        val expected =
            "SELECT * FROM ApplicationModel WHERE activitiesCount>0 AND (name LIKE '%test%' OR packageName LIKE '%test%') ORDER BY name COLLATE NOCASE ASC"
        assertEquals(expected, query.toString().trim())
    }

    @Test
    fun `query with search text and no disabled apps`() {
        val displayConfig = DisplayConfig(
            showDisabledApps = false,
            showSystemApps = true,
            sortBy = ApplicationModel.NAME,
            sortOrder = GetApplicationsQuery.ASC
        )

        val query = GetApplicationsQuery(displayConfig, "test")
        val expected =
            "SELECT * FROM ApplicationModel WHERE activitiesCount>0 AND (enabled=1) AND (name LIKE '%test%' OR packageName LIKE '%test%') ORDER BY name COLLATE NOCASE ASC"
        assertEquals(expected, query.toString().trim())
    }

    @Test
    fun `query with search text and no system apps`() {
        val displayConfig = DisplayConfig(
            showDisabledApps = true,
            showSystemApps = false,
            sortBy = ApplicationModel.NAME,
            sortOrder = GetApplicationsQuery.ASC
        )

        val query = GetApplicationsQuery(displayConfig, "test")
        val expected =
            "SELECT * FROM ApplicationModel WHERE activitiesCount>0 AND (system=0) AND (name LIKE '%test%' OR packageName LIKE '%test%') ORDER BY name COLLATE NOCASE ASC"
        assertEquals(expected, query.toString().trim())
    }

    @Test
    fun `query with search text and no disabled and no system apps`() {
        val displayConfig = DisplayConfig(
            showDisabledApps = false,
            showSystemApps = false,
            sortBy = ApplicationModel.NAME,
            sortOrder = GetApplicationsQuery.ASC
        )

        val query = GetApplicationsQuery(displayConfig, "test")
        val expected =
            "SELECT * FROM ApplicationModel WHERE activitiesCount>0 AND (enabled=1) AND (system=0) AND (name LIKE '%test%' OR packageName LIKE '%test%') ORDER BY name COLLATE NOCASE ASC"
        assertEquals(expected, query.toString().trim())
    }

    @Test
    fun `query with different sort order`() {
        val displayConfig = DisplayConfig(
            showDisabledApps = true,
            showSystemApps = true,
            sortBy = "packageName",
            sortOrder = GetApplicationsQuery.DESC
        )

        val query = GetApplicationsQuery(displayConfig)
        val expected =
            "SELECT * FROM ApplicationModel WHERE activitiesCount>0 ORDER BY packageName COLLATE NOCASE DESC"
        assertEquals(expected, query.toString().trim())
    }

    @Test
    fun `query with search text containing single quote`() {
        val displayConfig = DisplayConfig(
            showDisabledApps = true,
            showSystemApps = true,
            sortBy = ApplicationModel.NAME,
            sortOrder = GetApplicationsQuery.ASC
        )

        val query = GetApplicationsQuery(displayConfig, "test's")
        val expected =
            "SELECT * FROM ApplicationModel WHERE activitiesCount>0 AND (name LIKE '%test''s%' OR packageName LIKE '%test''s%') ORDER BY name COLLATE NOCASE ASC"
        assertEquals(expected, query.toString().trim())
    }
}
