package com.sdex.activityrunner.intent.converter

interface Converter<T> {

    fun convert(): T
}