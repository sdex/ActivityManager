package com.sdex.activityrunner

import com.sdex.activityrunner.intent.converter.IntegerListSerializer
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test

class IntegerListSerializerTest {

    @Test
    fun serialize() {
        val serializer = IntegerListSerializer()
        val actual = serializer.serialize(EXPECTED_LIST)
        assertEquals(actual, EXPECTED_STRING)
    }

    @Test
    fun serializeEmpty() {
        val serializer = IntegerListSerializer()
        val actual = serializer.serialize(ArrayList())
        assertEquals(actual, "")
    }

    @Test
    fun deserialize() {
        val serializer = IntegerListSerializer()
        val actual = serializer.deserialize(EXPECTED_STRING)
        assertEquals(actual.size, EXPECTED_LIST.size)
        for (i in actual.indices) {
            val integerActual = actual[i]
            val integerExpected = EXPECTED_LIST[i]
            assertEquals(integerActual, integerExpected)
        }
    }

    @Test
    fun deserializeEmpty() {
        val serializer = IntegerListSerializer()
        val actual = serializer.deserialize("")
        assertEquals(actual.size, 0)
    }

    companion object {
        private val EXPECTED_LIST = ArrayList<Int>()
        private const val EXPECTED_STRING = "1,2,3,4,9,8,7,6,5,0,0,0"

        @BeforeClass
        @JvmStatic
        fun setUp() {
            EXPECTED_LIST.clear()
            EXPECTED_LIST.add(1)
            EXPECTED_LIST.add(2)
            EXPECTED_LIST.add(3)
            EXPECTED_LIST.add(4)
            EXPECTED_LIST.add(9)
            EXPECTED_LIST.add(8)
            EXPECTED_LIST.add(7)
            EXPECTED_LIST.add(6)
            EXPECTED_LIST.add(5)
            EXPECTED_LIST.add(0)
            EXPECTED_LIST.add(0)
            EXPECTED_LIST.add(0)
        }
    }
}
