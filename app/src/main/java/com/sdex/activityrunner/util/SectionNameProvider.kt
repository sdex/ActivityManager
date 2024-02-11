package com.sdex.activityrunner.util

import com.sdex.activityrunner.db.cache.ApplicationModel

sealed interface SectionNameProvider {

    fun getSectionName(item: ApplicationModel): String
}

data object ApplicationSectionNameProvider : SectionNameProvider {

    override fun getSectionName(item: ApplicationModel): String {
        val name = item.name
        return if (name.isNullOrEmpty()) "" else name.first().uppercaseChar().toString()
    }
}

data object EmptySectionNameProvider : SectionNameProvider {

    override fun getSectionName(item: ApplicationModel): String = ""
}
