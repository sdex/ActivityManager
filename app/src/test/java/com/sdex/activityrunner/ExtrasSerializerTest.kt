package com.sdex.activityrunner

import com.sdex.activityrunner.intent.LaunchParamsExtra
import com.sdex.activityrunner.intent.LaunchParamsExtraType
import com.sdex.activityrunner.intent.converter.ExtrasSerializer
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test

class ExtrasSerializerTest {

    @Test
    fun serialize() {
        val extrasSerializer = ExtrasSerializer()
        val actual = extrasSerializer.serialize(EXPECTED_LIST)
        assertEquals(actual, EXPECTED_STRING)
    }

    @Test
    fun serializeEmpty() {
        val extrasSerializer = ExtrasSerializer()
        val actual = extrasSerializer.serialize(ArrayList())
        assertEquals(actual, "")
    }

    @Test
    fun deserialize() {
        val extrasSerializer = ExtrasSerializer()
        val actual = extrasSerializer.deserialize(EXPECTED_STRING)
        assertEquals(EXPECTED_LIST.size, actual.size)
        for (i in actual.indices) {
            val extra = actual[i]
            val expected = EXPECTED_LIST[i]
            assertEquals(expected.key, extra.key)
            assertEquals(expected.value, extra.value)
            assertEquals(expected.type, extra.type)
            assertEquals(expected.isArray, extra.isArray)
        }
    }

    @Test
    fun deserializeEmpty() {
        val extrasSerializer = ExtrasSerializer()
        val actual = extrasSerializer.deserialize("")
        assertEquals(actual.size, 0)
    }

    companion object {
        private val EXPECTED_LIST = ArrayList<LaunchParamsExtra>()
        private const val EXPECTED_STRING = "k0†3.3†4†true‡k1†v1†0†false‡k2†888†1†false"

        @BeforeClass
        @JvmStatic
        fun setUp() {
            EXPECTED_LIST.clear()

            val extra0 = LaunchParamsExtra(
                "k0", "3.3",
                LaunchParamsExtraType.DOUBLE, true
            )
            EXPECTED_LIST.add(extra0)

            val extra1 = LaunchParamsExtra(
                "k1", "v1",
                LaunchParamsExtraType.STRING, false
            )
            EXPECTED_LIST.add(extra1)

            val extra2 = LaunchParamsExtra(
                "k2", "888",
                LaunchParamsExtraType.INT, false
            )
            EXPECTED_LIST.add(extra2)
        }
    }
}
