package com.sdex.activityrunner.intent.converter

import java.util.*

class IntegerListSerializer {

    fun serialize(list: List<Int>): String {
        val stringBuilder = StringBuilder()
        for (i in list.indices) {
            stringBuilder.append(list[i])
            if (i != list.size - 1) {
                stringBuilder.append(DELIMITER)
            }
        }
        return stringBuilder.toString()
    }

    fun deserialize(input: String?): ArrayList<Int> {
        if (input == null || input.isEmpty()) {
            return ArrayList(0)
        }
        val split = input.split(DELIMITER.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val output = ArrayList<Int>(split.size)
        for (s in split) {
            output.add(Integer.parseInt(s))
        }
        return output
    }

    companion object {

        private const val DELIMITER = ","
    }
}
