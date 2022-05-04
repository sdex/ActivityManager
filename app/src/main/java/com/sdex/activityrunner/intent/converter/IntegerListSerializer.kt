package com.sdex.activityrunner.intent.converter

class IntegerListSerializer {

    fun serialize(list: List<Int>): String {
        return list.joinToString(DELIMITER)
    }

    fun deserialize(input: String?): ArrayList<Int> {
        if (input.isNullOrEmpty()) {
            return ArrayList(0)
        }
        val split = input.split(DELIMITER.toRegex())
            .dropLastWhile { it.isEmpty() }
            .map { it.toInt() }
        return ArrayList(split)
    }

    companion object {

        private const val DELIMITER = ","
    }
}
