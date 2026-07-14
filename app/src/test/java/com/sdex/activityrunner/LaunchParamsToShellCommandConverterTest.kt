package com.sdex.activityrunner

import com.sdex.activityrunner.intent.LaunchParams
import com.sdex.activityrunner.intent.LaunchParamsExtra
import com.sdex.activityrunner.intent.LaunchParamsExtraType
import com.sdex.activityrunner.intent.converter.LaunchParamsToShellCommandConverter
import com.sdex.activityrunner.intent.param.Category
import com.sdex.activityrunner.intent.param.Flag
import org.junit.Assert.assertEquals
import org.junit.Test

class LaunchParamsToShellCommandConverterTest {

    @Test
    fun `convert with component`() {
        val launchParams = LaunchParams(
            packageName = "com.example.app",
            className = "com.example.app.MainActivity",
        )

        val converter = LaunchParamsToShellCommandConverter(launchParams)
        val command = converter.convert()

        assertEquals(
            "am start -n com.example.app/com.example.app.MainActivity " +
                "-a android.intent.action.MAIN",
            command,
        )
    }

    @Test
    fun `convert with package only`() {
        val launchParams = LaunchParams(
            packageName = "com.example.app",
            action = "android.intent.action.VIEW",
        )

        val converter = LaunchParamsToShellCommandConverter(launchParams)
        val command = converter.convert()

        assertEquals("am start -a android.intent.action.VIEW com.example.app", command)
    }

    @Test
    fun `convert with data and mime type`() {
        val launchParams = LaunchParams(
            action = "android.intent.action.VIEW",
            data = "https://example.com/path?query=value",
            mimeType = "text/plain",
        )

        val converter = LaunchParamsToShellCommandConverter(launchParams)
        val command = converter.convert()

        assertEquals(
            "am start -a android.intent.action.VIEW " +
                "-d 'https://example.com/path?query=value' -t text/plain",
            command,
        )
    }

    @Test
    fun `convert ignores mime type without data`() {
        val launchParams = LaunchParams(mimeType = "text/plain")

        val converter = LaunchParamsToShellCommandConverter(launchParams)
        val command = converter.convert()

        assertEquals("am start -a android.intent.action.MAIN", command)
    }

    @Test
    fun `convert with categories`() {
        val position = Category.list().indexOf("CATEGORY_DEFAULT")
        val launchParams = LaunchParams(categories = listOf(position))

        val converter = LaunchParamsToShellCommandConverter(launchParams)
        val command = converter.convert()

        assertEquals(
            "am start -a android.intent.action.MAIN -c android.intent.category.DEFAULT",
            command,
        )
    }

    @Test
    fun `convert with flags combined`() {
        val positions = listOf(
            Flag.list().indexOf("FLAG_ACTIVITY_NEW_TASK"),
            Flag.list().indexOf("FLAG_ACTIVITY_CLEAR_TOP"),
        )
        val launchParams = LaunchParams(flags = positions)

        val converter = LaunchParamsToShellCommandConverter(launchParams)
        val command = converter.convert()

        // FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TOP = 0x10000000 or 0x04000000
        assertEquals("am start -a android.intent.action.MAIN -f 335544320", command)
    }

    @Test
    fun `convert with all extra types`() {
        val extras = listOf(
            LaunchParamsExtra("str", "text value", LaunchParamsExtraType.STRING),
            LaunchParamsExtra("int", "42", LaunchParamsExtraType.INT),
            LaunchParamsExtra("long", "9999999999", LaunchParamsExtraType.LONG),
            LaunchParamsExtra("float", "3.14", LaunchParamsExtraType.FLOAT),
            LaunchParamsExtra("double", "2.71828", LaunchParamsExtraType.DOUBLE),
            LaunchParamsExtra("bool", "true", LaunchParamsExtraType.BOOLEAN),
        )
        val launchParams = LaunchParams(extras = extras)

        val converter = LaunchParamsToShellCommandConverter(launchParams)
        val command = converter.convert()

        assertEquals(
            "am start -a android.intent.action.MAIN " +
                "--es str 'text value' " +
                "--ei int 42 " +
                "--el long 9999999999 " +
                "--ef float 3.14 " +
                "--ed double 2.71828 " +
                "--ez bool true",
            command,
        )
    }

    @Test
    fun `convert with array extras`() {
        val extras = listOf(
            LaunchParamsExtra("ints", "1,2,3", LaunchParamsExtraType.INT, true),
            LaunchParamsExtra("longs", "1,2", LaunchParamsExtraType.LONG, true),
            LaunchParamsExtra("floats", "1.5,2.5", LaunchParamsExtraType.FLOAT, true),
            LaunchParamsExtra("doubles", "1.5,2.5", LaunchParamsExtraType.DOUBLE, true),
            LaunchParamsExtra("strings", "a,b,c", LaunchParamsExtraType.STRING, true),
        )
        val launchParams = LaunchParams(extras = extras)

        val converter = LaunchParamsToShellCommandConverter(launchParams)
        val command = converter.convert()

        assertEquals(
            "am start -a android.intent.action.MAIN " +
                "--eia ints 1,2,3 " +
                "--ela longs 1,2 " +
                "--efa floats 1.5,2.5 " +
                "--eda doubles 1.5,2.5 " +
                "--esa strings a,b,c",
            command,
        )
    }

    @Test
    fun `convert skips boolean array extras`() {
        val extras = listOf(
            LaunchParamsExtra("bools", "true,false", LaunchParamsExtraType.BOOLEAN, true),
            LaunchParamsExtra("int", "42", LaunchParamsExtraType.INT),
        )
        val launchParams = LaunchParams(extras = extras)

        val converter = LaunchParamsToShellCommandConverter(launchParams)
        val command = converter.convert()

        assertEquals("am start -a android.intent.action.MAIN --ei int 42", command)
    }

    @Test
    fun `getUnsupportedExtras returns boolean arrays only`() {
        val booleanArray = LaunchParamsExtra("bools", "true,false", LaunchParamsExtraType.BOOLEAN, true)
        val launchParams = LaunchParams(
            extras = listOf(
                booleanArray,
                LaunchParamsExtra("ints", "1,2", LaunchParamsExtraType.INT, true),
                LaunchParamsExtra("bool", "true", LaunchParamsExtraType.BOOLEAN),
            ),
        )

        val unsupported = LaunchParamsToShellCommandConverter.getUnsupportedExtras(launchParams)

        assertEquals(listOf(booleanArray), unsupported)
    }

    @Test
    fun `convert escapes single quotes`() {
        val extras = listOf(
            LaunchParamsExtra("key", "it's a value", LaunchParamsExtraType.STRING),
        )
        val launchParams = LaunchParams(extras = extras)

        val converter = LaunchParamsToShellCommandConverter(launchParams)
        val command = converter.convert()

        assertEquals(
            "am start -a android.intent.action.MAIN --es key 'it'\\''s a value'",
            command,
        )
    }

    @Test
    fun `convert quotes class name with inner class separator`() {
        val launchParams = LaunchParams(
            packageName = "com.example.app",
            className = "com.example.app.MainActivity\$Inner",
        )

        val converter = LaunchParamsToShellCommandConverter(launchParams)
        val command = converter.convert()

        assertEquals(
            "am start -n 'com.example.app/com.example.app.MainActivity\$Inner' " +
                "-a android.intent.action.MAIN",
            command,
        )
    }
}
