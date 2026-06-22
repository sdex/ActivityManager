package com.sdex.activityrunner

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.sdex.activityrunner.extensions.getFlagsList
import com.sdex.activityrunner.intent.LaunchParams
import com.sdex.activityrunner.intent.LaunchParamsExtra
import com.sdex.activityrunner.intent.LaunchParamsExtraType
import com.sdex.activityrunner.intent.converter.LaunchParamsToIntentConverter
import com.sdex.activityrunner.intent.getCategoriesValues
import com.sdex.activityrunner.intent.param.Category
import com.sdex.activityrunner.intent.param.Flag
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class LaunchParamsToIntentConverterTest {

    @Test
    fun convertSetsExplicitAction() {
        val intent = LaunchParams(action = Intent.ACTION_VIEW).toIntent()

        assertEquals(Intent.ACTION_VIEW, intent.action)
    }

    @Test
    fun convertUsesMainActionWhenActionIsNotSet() {
        val intent = LaunchParams().toIntent()

        assertEquals(Intent.ACTION_MAIN, intent.action)
    }

    @Test
    fun convertUsesMainActionWhenActionIsEmpty() {
        val intent = LaunchParams(action = "").toIntent()

        assertEquals(Intent.ACTION_MAIN, intent.action)
    }

    @Test
    fun convertAddsCategories() {
        val launchParams = LaunchParams(categories = categories)
        val intent = launchParams.toIntent()

        assertCategoriesEquals(launchParams, intent)
    }

    @Test
    fun convertDoesNotAddCategoriesWhenCategoriesAreEmpty() {
        val intent = LaunchParams(categories = emptyList()).toIntent()

        assertNull(intent.categories)
    }

    @Test
    fun convertSetsData() {
        val intent = LaunchParams(data = "test_data_string").toIntent()

        assertEquals(Uri.parse("test_data_string"), intent.data)
    }

    @Test
    fun convertSetsMimeTypeWhenDataIsSet() {
        val intent = LaunchParams(
            data = "data",
            mimeType = "image/png",
        ).toIntent()

        assertEquals("image/png", intent.type)
    }

    @Test
    fun convertIgnoresMimeTypeWhenDataIsNotSet() {
        val intent = LaunchParams(mimeType = "image/png").toIntent()

        assertNull(intent.data)
        assertNull(intent.type)
    }

    @Test
    fun convertIgnoresDataAndMimeTypeWhenDataIsEmpty() {
        val intent = LaunchParams(
            data = "",
            mimeType = "image/png",
        ).toIntent()

        assertNull(intent.data)
        assertNull(intent.type)
    }

    @Test
    fun convertSetsPackageWithoutComponentWhenOnlyPackageNameIsSet() {
        val intent = LaunchParams(packageName = "test_package_name").toIntent()

        assertEquals("test_package_name", intent.`package`)
        assertNull(intent.component)
    }

    @Test
    fun convertIgnoresClassNameWhenPackageNameIsMissing() {
        val intent = LaunchParams(className = "test_class_name").toIntent()

        assertNull(intent.`package`)
        assertNull(intent.component)
    }

    @Test
    fun convertIgnoresEmptyPackageName() {
        val intent = LaunchParams(packageName = "").toIntent()

        assertNull(intent.`package`)
        assertNull(intent.component)
    }

    @Test
    fun convertDoesNotSetComponentWhenClassNameIsEmpty() {
        val intent = LaunchParams(
            packageName = "test_package_name",
            className = "",
        ).toIntent()

        assertEquals("test_package_name", intent.`package`)
        assertNull(intent.component)
    }

    @Test
    fun convertSetsComponentWhenClassAndPackageNamesAreSet() {
        val expected = ComponentName("test_package_name", "test_class_name")
        val intent = LaunchParams(
            packageName = expected.packageName,
            className = expected.className,
        ).toIntent()

        assertEquals(expected, intent.component)
    }

    @Test
    fun convertAddsExtras() {
        val intent = LaunchParams(extras = extras).toIntent()

        assertExtrasEquals(intent)
    }

    @Test
    fun convertDoesNotAddExtrasWhenExtrasAreEmpty() {
        val intent = LaunchParams(extras = emptyList()).toIntent()

        assertNull(intent.extras)
    }

    @Test
    fun convertSkipsExtrasWithInvalidNumericValues() {
        val intent = LaunchParams(
            extras = listOf(
                LaunchParamsExtra("int", "not_int", LaunchParamsExtraType.INT, false),
                LaunchParamsExtra("long", "not_long", LaunchParamsExtraType.LONG, false),
                LaunchParamsExtra("float", "not_float", LaunchParamsExtraType.FLOAT, false),
                LaunchParamsExtra("double", "not_double", LaunchParamsExtraType.DOUBLE, false),
            ),
        ).toIntent()

        assertNull(intent.extras)
    }

    @Test
    fun convertAddsFlags() {
        val launchParams = LaunchParams(flags = flags)
        val intent = launchParams.toIntent()

        assertFlagsEquals(launchParams, intent)
    }

    @Test
    fun convertDoesNotAddFlagsWhenFlagsAreEmpty() {
        val intent = LaunchParams(flags = emptyList()).toIntent()

        assertEquals(0, intent.flags)
    }

    @Test
    fun convertMapsAllSupportedFields() {
        val expected = ComponentName("pkg_name", "cls_name")
        val launchParams = LaunchParams(
            action = Intent.ACTION_ASSIST,
            packageName = expected.packageName,
            className = expected.className,
            data = "data",
            mimeType = "type",
            flags = flags,
            categories = categories,
            extras = extras,
        )

        val intent = launchParams.toIntent()

        assertEquals(Intent.ACTION_ASSIST, intent.action)
        assertEquals(expected, intent.component)
        assertEquals(Uri.parse("data"), intent.data)
        assertEquals("type", intent.type)
        assertFlagsEquals(launchParams, intent)
        assertCategoriesEquals(launchParams, intent)
        assertExtrasEquals(intent)
    }

    private fun LaunchParams.toIntent(): Intent = LaunchParamsToIntentConverter(this).convert()

    private fun assertCategoriesEquals(launchParams: LaunchParams, intent: Intent) {
        val intentCategories = requireNotNull(intent.categories)
        val categoriesValues = Category.list(launchParams.getCategoriesValues())

        assertEquals(categoriesValues.size, intentCategories.size)
        assertTrue(intentCategories.all { it in categoriesValues })
    }

    private fun assertFlagsEquals(launchParams: LaunchParams, intent: Intent) {
        val intentFlags = intent.getFlagsList()
        launchParams.flags.forEach { position ->
            val flagName = Flag.list()[position]
            assertTrue(intentFlags.contains(flagName))
        }
    }

    private fun assertExtrasEquals(intent: Intent) {
        val intentExtras = intent.extras
        assertNotNull(intentExtras)

        requireNotNull(intentExtras)
        assertEquals("str", intentExtras.getString("k00"))
        assertEquals("str233", intentExtras.getString("k01"))

        assertEquals(1, intentExtras.getInt("k10"))
        assertEquals(0, intentExtras.getInt("k11"))
        assertEquals(-1, intentExtras.getInt("k12"))

        assertEquals(1L, intentExtras.getLong("k20"))
        assertEquals(0L, intentExtras.getLong("k21"))
        assertEquals(-1L, intentExtras.getLong("k22"))

        assertEquals(0.0f.toBits(), intentExtras.getFloat("k30").toBits())
        assertEquals(1.0f.toBits(), intentExtras.getFloat("k31").toBits())
        assertEquals(0.1f.toBits(), intentExtras.getFloat("k32").toBits())
        assertEquals((-0.2f).toBits(), intentExtras.getFloat("k33").toBits())

        assertEquals(0.0.toBits(), intentExtras.getDouble("k40").toBits())
        assertEquals(1.0.toBits(), intentExtras.getDouble("k41").toBits())
        assertEquals(0.1.toBits(), intentExtras.getDouble("k42").toBits())
        assertEquals((-0.2).toBits(), intentExtras.getDouble("k43").toBits())

        assertTrue(intentExtras.getBoolean("k50"))
        assertFalse(intentExtras.getBoolean("k51"))
        assertFalse(intentExtras.containsKey("k52"))
        assertFalse(intentExtras.containsKey("k53"))
    }

    private val categories: List<Int>
        get() = listOf(
            Category.list().indexOf("CATEGORY_APP_BROWSER"),
            Category.list().indexOf("CATEGORY_DEFAULT"),
            Category.list().indexOf("CATEGORY_LAUNCHER"),
            Category.list().indexOf("CATEGORY_PREFERENCE"),
        )

    private val flags: List<Int>
        get() = listOf(
            Flag.list().indexOf("FLAG_ACTIVITY_CLEAR_TASK"),
            Flag.list().indexOf("FLAG_ACTIVITY_NEW_TASK"),
            Flag.list().indexOf("FLAG_ACTIVITY_NO_HISTORY"),
            Flag.list().indexOf("FLAG_ACTIVITY_SINGLE_TOP"),
            Flag.list().indexOf("FLAG_GRANT_READ_URI_PERMISSION"),
        )

    private val extras: List<LaunchParamsExtra>
        get() = listOf(
            LaunchParamsExtra("k00", "str", LaunchParamsExtraType.STRING, false),
            LaunchParamsExtra("k01", "str233", LaunchParamsExtraType.STRING, false),

            LaunchParamsExtra("k10", "1", LaunchParamsExtraType.INT, false),
            LaunchParamsExtra("k11", "0", LaunchParamsExtraType.INT, false),
            LaunchParamsExtra("k12", "-1", LaunchParamsExtraType.INT, false),

            LaunchParamsExtra("k20", "1", LaunchParamsExtraType.LONG, false),
            LaunchParamsExtra("k21", "0", LaunchParamsExtraType.LONG, false),
            LaunchParamsExtra("k22", "-1", LaunchParamsExtraType.LONG, false),

            LaunchParamsExtra("k30", "0.0", LaunchParamsExtraType.FLOAT, false),
            LaunchParamsExtra("k31", "1.0", LaunchParamsExtraType.FLOAT, false),
            LaunchParamsExtra("k32", "0.1", LaunchParamsExtraType.FLOAT, false),
            LaunchParamsExtra("k33", "-0.2", LaunchParamsExtraType.FLOAT, false),

            LaunchParamsExtra("k40", "0.0", LaunchParamsExtraType.DOUBLE, false),
            LaunchParamsExtra("k41", "1.0", LaunchParamsExtraType.DOUBLE, false),
            LaunchParamsExtra("k42", "0.1", LaunchParamsExtraType.DOUBLE, false),
            LaunchParamsExtra("k43", "-0.2", LaunchParamsExtraType.DOUBLE, false),

            LaunchParamsExtra("k50", "true", LaunchParamsExtraType.BOOLEAN, false),
            LaunchParamsExtra("k51", "false", LaunchParamsExtraType.BOOLEAN, false),
            LaunchParamsExtra("k52", "kek", LaunchParamsExtraType.BOOLEAN, false),
            LaunchParamsExtra("k53", "33", LaunchParamsExtraType.BOOLEAN, false),
        )
}
