package com.sdex.activityrunner.intent.converter

import com.sdex.activityrunner.intent.LaunchParamsExtra

class ExtrasSerializer {

    fun serialize(list: List<LaunchParamsExtra>): String {
        val stringBuilder = StringBuilder()
        for (i in list.indices) {
            val extra = list[i]
            stringBuilder.append(extra.key).append(DELIMITER_KEY_VALUE)
                .append(extra.value).append(DELIMITER_KEY_VALUE)
                .append(extra.type).append(DELIMITER_KEY_VALUE)
                .append(extra.isArray)
            if (i != list.size - 1) {
                stringBuilder.append(DELIMITER_EXTRA)
            }
        }
        return stringBuilder.toString()
    }

    fun deserialize(input: String?): ArrayList<LaunchParamsExtra> {
        if (input.isNullOrEmpty()) {
            return ArrayList()
        }
        val extras = input.split(DELIMITER_EXTRA.toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val output = ArrayList<LaunchParamsExtra>(extras.size)
        for (extra in extras) {
            val values = extra.split(DELIMITER_KEY_VALUE.toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()
            val paramsExtra = LaunchParamsExtra(
                values[0], values[1], Integer.parseInt(values[2]),
                java.lang.Boolean.parseBoolean(values[3])
            )
            output.add(paramsExtra)
        }
        return output
    }

    companion object {

        private const val DELIMITER_KEY_VALUE = "†"
        private const val DELIMITER_EXTRA = "‡"
    }
}
