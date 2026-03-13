package com.sdex.activityrunner

import com.sdex.activityrunner.intent.converter.IntegerListSerializer
import com.sdex.activityrunner.intent.converter.IntegerListSerializer.Companion.DELIMITER
import org.junit.Assert.assertEquals
import org.junit.Test

class IntegerListSerializerTest {

    private val serializer = IntegerListSerializer()

    @Test
    fun `serialize empty list returns empty string`() {
        val result = serializer.serialize(emptyList())
        assertEquals("", result)
    }

    @Test
    fun `deserialize empty string returns empty list`() {
        val result = serializer.deserialize("")
        assertEquals(emptyList<Int>(), result)
    }

    @Test
    fun `deserialize null returns empty list`() {
        val result = serializer.deserialize(null)
        assertEquals(emptyList<Int>(), result)
    }

    @Test
    fun `serialize single integer`() {
        val result = serializer.serialize(listOf(42))
        assertEquals("42", result)
    }

    @Test
    fun `deserialize single integer`() {
        val result = serializer.deserialize("42")
        assertEquals(listOf(42), result)
    }

    @Test
    fun `serialize multiple integers`() {
        val result = serializer.serialize(MULTIPLE_INTEGERS)
        assertEquals(MULTIPLE_INTEGERS_SERIALIZED, result)
    }

    @Test
    fun `deserialize multiple integers`() {
        val result = serializer.deserialize(MULTIPLE_INTEGERS_SERIALIZED)
        assertEquals(MULTIPLE_INTEGERS, result)
    }

    @Test
    fun `round-trip serialization preserves all integers`() {
        val serialized = serializer.serialize(MULTIPLE_INTEGERS)
        val deserialized = serializer.deserialize(serialized)
        assertEquals(MULTIPLE_INTEGERS, deserialized)
    }

    @Test
    fun `serialize negative integers`() {
        val result = serializer.serialize(listOf(-1, -42, -999))
        assertEquals("-1,-42,-999", result)
    }

    @Test
    fun `deserialize negative integers`() {
        val result = serializer.deserialize("-1,-42,-999")
        assertEquals(listOf(-1, -42, -999), result)
    }

    @Test
    fun `serialize zero`() {
        val result = serializer.serialize(listOf(0))
        assertEquals("0", result)
    }

    @Test
    fun `deserialize zero`() {
        val result = serializer.deserialize("0")
        assertEquals(listOf(0), result)
    }

    @Test
    fun `serialize large integers`() {
        val largeInts = listOf(Int.MAX_VALUE, Int.MIN_VALUE)
        val result = serializer.serialize(largeInts)
        assertEquals("${Int.MAX_VALUE},${Int.MIN_VALUE}", result)
    }

    @Test
    fun `deserialize large integers`() {
        val input = "${Int.MAX_VALUE},${Int.MIN_VALUE}"
        val result = serializer.deserialize(input)
        assertEquals(listOf(Int.MAX_VALUE, Int.MIN_VALUE), result)
    }

    @Test
    fun `serialize mixed positive negative and zero`() {
        val result = serializer.serialize(listOf(-5, 0, 5, -10, 10))
        assertEquals("-5,0,5,-10,10", result)
    }

    @Test
    fun `deserialize mixed positive negative and zero`() {
        val result = serializer.deserialize("-5,0,5,-10,10")
        assertEquals(listOf(-5, 0, 5, -10, 10), result)
    }

    @Test
    fun `deserialize handles trailing delimiter`() {
        val result = serializer.deserialize("1,2,3,")
        assertEquals(listOf(1, 2, 3), result)
    }

    @Test
    fun `deserialize handles multiple trailing delimiters`() {
        val result = serializer.deserialize("1,2,,,")
        assertEquals(listOf(1, 2), result)
    }

    @Test
    fun `serialize duplicate integers`() {
        val result = serializer.serialize(listOf(1, 1, 2, 2, 3, 3))
        assertEquals("1,1,2,2,3,3", result)
    }

    @Test
    fun `deserialize duplicate integers`() {
        val result = serializer.deserialize("1,1,2,2,3,3")
        assertEquals(listOf(1, 1, 2, 2, 3, 3), result)
    }

    @Test
    fun `serialize consecutive integers`() {
        val result = serializer.serialize(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
        assertEquals("1,2,3,4,5,6,7,8,9,10", result)
    }

    @Test
    fun `deserialize consecutive integers`() {
        val result = serializer.deserialize("1,2,3,4,5,6,7,8,9,10")
        assertEquals(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), result)
    }

    @Test
    fun `serialize android intent flags`() {
        // Common Android Intent flags
        val flags = listOf(
            268435456,  // FLAG_ACTIVITY_NEW_TASK
            67108864,   // FLAG_ACTIVITY_CLEAR_TOP
            536870912,  // FLAG_ACTIVITY_SINGLE_TOP
            134217728,  // FLAG_ACTIVITY_NO_HISTORY
        )
        val result = serializer.serialize(flags)
        assertEquals("268435456,67108864,536870912,134217728", result)
    }

    @Test
    fun `deserialize android intent flags`() {
        val input = "268435456,67108864,536870912,134217728"
        val result = serializer.deserialize(input)
        assertEquals(
            listOf(268435456, 67108864, 536870912, 134217728),
            result,
        )
    }

    companion object {
        private val MULTIPLE_INTEGERS = listOf(1, 2, 3, 4, 9, 8, 7, 6, 5, 0, 0, 0)
        private val MULTIPLE_INTEGERS_SERIALIZED = MULTIPLE_INTEGERS.joinToString(DELIMITER)
    }
}
