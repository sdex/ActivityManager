package com.sdex.activityrunner

import com.sdex.activityrunner.intent.LaunchParamsExtra
import com.sdex.activityrunner.intent.LaunchParamsExtraType
import com.sdex.activityrunner.intent.converter.ExtrasSerializer
import com.sdex.activityrunner.intent.converter.ExtrasSerializer.Companion.DELIMITER_EXTRA
import com.sdex.activityrunner.intent.converter.ExtrasSerializer.Companion.DELIMITER_KEY_VALUE
import org.junit.Assert.assertEquals
import org.junit.Test

class ExtrasSerializerTest {

    private val serializer = ExtrasSerializer()

    @Test
    fun `serialize empty list returns empty string`() {
        val result = serializer.serialize(emptyList())
        assertEquals("", result)
    }

    @Test
    fun `deserialize empty string returns empty list`() {
        val result = serializer.deserialize("")
        assertEquals(emptyList<LaunchParamsExtra>(), result)
    }

    @Test
    fun `deserialize null returns empty list`() {
        val result = serializer.deserialize(null)
        assertEquals(emptyList<LaunchParamsExtra>(), result)
    }

    @Test
    fun `serialize single extra`() {
        val extras = listOf(LaunchParamsExtra("key", "value", LaunchParamsExtraType.STRING, false))
        val result = serializer.serialize(extras)
        assertEquals(buildSerialized("key", "value", LaunchParamsExtraType.STRING, false), result)
    }

    @Test
    fun `serialize multiple extras`() {
        val result = serializer.serialize(MULTIPLE_EXTRAS)
        assertEquals(MULTIPLE_EXTRAS_SERIALIZED, result)
    }

    @Test
    fun `deserialize single extra`() {
        val input = buildSerialized("key", "value", LaunchParamsExtraType.STRING, false)
        val result = serializer.deserialize(input)
        val expected =
            listOf(LaunchParamsExtra("key", "value", LaunchParamsExtraType.STRING, false))
        assertEquals(expected, result)
    }

    @Test
    fun `deserialize multiple extras`() {
        val result = serializer.deserialize(MULTIPLE_EXTRAS_SERIALIZED)
        assertEquals(MULTIPLE_EXTRAS, result)
    }

    @Test
    fun `round-trip serialization preserves all extras`() {
        val serialized = serializer.serialize(MULTIPLE_EXTRAS)
        val deserialized = serializer.deserialize(serialized)
        assertEquals(MULTIPLE_EXTRAS, deserialized)
    }

    @Test
    fun `serialize all extra types`() {
        val extras = listOf(
            LaunchParamsExtra("str", "text", LaunchParamsExtraType.STRING, false),
            LaunchParamsExtra("int", "42", LaunchParamsExtraType.INT, false),
            LaunchParamsExtra("long", "9999999999", LaunchParamsExtraType.LONG, false),
            LaunchParamsExtra("float", "3.14", LaunchParamsExtraType.FLOAT, false),
            LaunchParamsExtra("double", "2.718281828", LaunchParamsExtraType.DOUBLE, false),
            LaunchParamsExtra("bool", "true", LaunchParamsExtraType.BOOLEAN, false),
        )
        val result = serializer.serialize(extras)
        val expected = listOf(
            buildSerialized("str", "text", LaunchParamsExtraType.STRING, false),
            buildSerialized("int", "42", LaunchParamsExtraType.INT, false),
            buildSerialized("long", "9999999999", LaunchParamsExtraType.LONG, false),
            buildSerialized("float", "3.14", LaunchParamsExtraType.FLOAT, false),
            buildSerialized("double", "2.718281828", LaunchParamsExtraType.DOUBLE, false),
            buildSerialized("bool", "true", LaunchParamsExtraType.BOOLEAN, false),
        ).joinToString(DELIMITER_EXTRA)
        assertEquals(expected, result)
    }

    @Test
    fun `deserialize all extra types`() {
        val input = listOf(
            buildSerialized("str", "text", LaunchParamsExtraType.STRING, false),
            buildSerialized("int", "42", LaunchParamsExtraType.INT, false),
            buildSerialized("long", "9999999999", LaunchParamsExtraType.LONG, false),
            buildSerialized("float", "3.14", LaunchParamsExtraType.FLOAT, false),
            buildSerialized("double", "2.718281828", LaunchParamsExtraType.DOUBLE, false),
            buildSerialized("bool", "true", LaunchParamsExtraType.BOOLEAN, false),
        ).joinToString(DELIMITER_EXTRA)
        val result = serializer.deserialize(input)
        val expected = listOf(
            LaunchParamsExtra("str", "text", LaunchParamsExtraType.STRING, false),
            LaunchParamsExtra("int", "42", LaunchParamsExtraType.INT, false),
            LaunchParamsExtra("long", "9999999999", LaunchParamsExtraType.LONG, false),
            LaunchParamsExtra("float", "3.14", LaunchParamsExtraType.FLOAT, false),
            LaunchParamsExtra("double", "2.718281828", LaunchParamsExtraType.DOUBLE, false),
            LaunchParamsExtra("bool", "true", LaunchParamsExtraType.BOOLEAN, false),
        )
        assertEquals(expected, result)
    }

    @Test
    fun `serialize with isArray true`() {
        val extras = listOf(
            LaunchParamsExtra("arr", "[1,2,3]", LaunchParamsExtraType.INT, true),
        )
        val result = serializer.serialize(extras)
        assertEquals(buildSerialized("arr", "[1,2,3]", LaunchParamsExtraType.INT, true), result)
    }

    @Test
    fun `serialize with isArray false`() {
        val extras = listOf(
            LaunchParamsExtra("single", "42", LaunchParamsExtraType.INT, false),
        )
        val result = serializer.serialize(extras)
        assertEquals(buildSerialized("single", "42", LaunchParamsExtraType.INT, false), result)
    }

    @Test
    fun `deserialize with isArray true`() {
        val input = buildSerialized("arr", "[1,2,3]", LaunchParamsExtraType.INT, true)
        val result = serializer.deserialize(input)
        val expected = listOf(LaunchParamsExtra("arr", "[1,2,3]", LaunchParamsExtraType.INT, true))
        assertEquals(expected, result)
    }

    @Test
    fun `deserialize with isArray false`() {
        val input = buildSerialized("single", "42", LaunchParamsExtraType.INT, false)
        val result = serializer.deserialize(input)
        val expected = listOf(LaunchParamsExtra("single", "42", LaunchParamsExtraType.INT, false))
        assertEquals(expected, result)
    }

    @Test
    fun `serialize preserves extra field order`() {
        val extra = LaunchParamsExtra("myKey", "myValue", LaunchParamsExtraType.DOUBLE, true)
        val result = serializer.serialize(listOf(extra))
        assertEquals(
            buildSerialized("myKey", "myValue", LaunchParamsExtraType.DOUBLE, true),
            result,
        )
    }

    @Test
    fun `deserialize handles special characters in values`() {
        val extra = LaunchParamsExtra(
            "key",
            "value with spaces and symbols !@#$%",
            LaunchParamsExtraType.STRING,
            false,
        )
        val serialized = serializer.serialize(listOf(extra))
        val deserialized = serializer.deserialize(serialized)
        assertEquals(listOf(extra), deserialized)
    }

    @Test
    fun `serialize and deserialize empty values`() {
        val extra = LaunchParamsExtra("empty", "", LaunchParamsExtraType.STRING, false)
        val serialized = serializer.serialize(listOf(extra))
        val deserialized = serializer.deserialize(serialized)
        assertEquals(listOf(extra), deserialized)
    }

    @Test
    fun `deserialize handles trailing delimiter`() {
        val input =
            buildSerialized("key", "value", LaunchParamsExtraType.STRING, false) + DELIMITER_EXTRA
        val result = serializer.deserialize(input)
        val expected =
            listOf(LaunchParamsExtra("key", "value", LaunchParamsExtraType.STRING, false))
        assertEquals(expected, result)
    }

    companion object {
        private val MULTIPLE_EXTRAS = listOf(
            LaunchParamsExtra("k0", "3.3", LaunchParamsExtraType.DOUBLE, true),
            LaunchParamsExtra("k1", "v1", LaunchParamsExtraType.STRING, false),
            LaunchParamsExtra("k2", "888", LaunchParamsExtraType.INT, false),
        )

        private fun buildSerialized(
            key: String,
            value: String,
            type: Int,
            isArray: Boolean,
        ): String {
            return "$key$DELIMITER_KEY_VALUE$value$DELIMITER_KEY_VALUE$type$DELIMITER_KEY_VALUE$isArray"
        }

        private val MULTIPLE_EXTRAS_SERIALIZED = listOf(
            buildSerialized("k0", "3.3", LaunchParamsExtraType.DOUBLE, true),
            buildSerialized("k1", "v1", LaunchParamsExtraType.STRING, false),
            buildSerialized("k2", "888", LaunchParamsExtraType.INT, false),
        ).joinToString(DELIMITER_EXTRA)
    }
}
