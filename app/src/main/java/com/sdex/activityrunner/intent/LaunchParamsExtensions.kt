package com.sdex.activityrunner.intent

import com.sdex.activityrunner.intent.param.Category
import com.sdex.activityrunner.intent.param.Flag

fun LaunchParams.getCategoriesValues(): MutableList<String> {
    val list = Category.list()
    val result = ArrayList<String>(categories.size)
    for (position in categories) {
        result.add(list[position])
    }
    return result
}

fun LaunchParams.getFlagsValues(): MutableList<String> {
    val list = Flag.list()
    val result = ArrayList<String>(flags.size)
    for (position in flags) {
        result.add(list[position])
    }
    return result
}