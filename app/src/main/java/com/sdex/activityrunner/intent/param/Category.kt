package com.sdex.activityrunner.intent.param

import android.content.Intent
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES

object Category {

    private var list: ArrayList<String>? = null
    private val CATEGORIES = object : HashMap<String, String>() {
        init {
            put("CATEGORY_ALTERNATIVE", Intent.CATEGORY_ALTERNATIVE)
            put("CATEGORY_APP_BROWSER", Intent.CATEGORY_APP_BROWSER)
            put("CATEGORY_APP_CALCULATOR", Intent.CATEGORY_APP_CALCULATOR)
            put("CATEGORY_APP_CALENDAR", Intent.CATEGORY_APP_CALENDAR)
            put("CATEGORY_APP_CONTACTS", Intent.CATEGORY_APP_CONTACTS)
            put("CATEGORY_APP_EMAIL", Intent.CATEGORY_APP_EMAIL)
            put("CATEGORY_APP_GALLERY", Intent.CATEGORY_APP_GALLERY)
            put("CATEGORY_APP_MAPS", Intent.CATEGORY_APP_MAPS)
            put("CATEGORY_APP_MARKET", Intent.CATEGORY_APP_MARKET)
            put("CATEGORY_APP_MESSAGING", Intent.CATEGORY_APP_MESSAGING)
            put("CATEGORY_APP_MUSIC", Intent.CATEGORY_APP_MUSIC)
            put("CATEGORY_BROWSABLE", Intent.CATEGORY_BROWSABLE)
            put("CATEGORY_CAR_DOCK", Intent.CATEGORY_CAR_DOCK)
            put("CATEGORY_CAR_MODE", Intent.CATEGORY_CAR_MODE)
            put("CATEGORY_DEFAULT", Intent.CATEGORY_DEFAULT)
            put("CATEGORY_DESK_DOCK", Intent.CATEGORY_DESK_DOCK)
            put("CATEGORY_DEVELOPMENT_PREFERENCE", Intent.CATEGORY_DEVELOPMENT_PREFERENCE)
            put("CATEGORY_EMBED", Intent.CATEGORY_EMBED)
            put(
                "CATEGORY_FRAMEWORK_INSTRUMENTATION_TEST",
                Intent.CATEGORY_FRAMEWORK_INSTRUMENTATION_TEST
            )
            put("CATEGORY_HE_DESK_DOCK", Intent.CATEGORY_HE_DESK_DOCK)
            put("CATEGORY_HOME", Intent.CATEGORY_HOME)
            put("CATEGORY_INFO", Intent.CATEGORY_INFO)
            put("CATEGORY_LAUNCHER", Intent.CATEGORY_LAUNCHER)
            put("CATEGORY_LE_DESK_DOCK", Intent.CATEGORY_LE_DESK_DOCK)
            put("CATEGORY_LEANBACK_LAUNCHER", Intent.CATEGORY_LEANBACK_LAUNCHER)
            put("CATEGORY_MONKEY", Intent.CATEGORY_MONKEY)
            put("CATEGORY_OPENABLE", Intent.CATEGORY_OPENABLE)
            put("CATEGORY_PREFERENCE", Intent.CATEGORY_PREFERENCE)
            put("CATEGORY_SAMPLE_CODE", Intent.CATEGORY_SAMPLE_CODE)
            put("CATEGORY_SELECTED_ALTERNATIVE", Intent.CATEGORY_SELECTED_ALTERNATIVE)
            put("CATEGORY_TAB", Intent.CATEGORY_TAB)
            put("CATEGORY_TEST", Intent.CATEGORY_TEST)
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                put("CATEGORY_TYPED_OPENABLE", Intent.CATEGORY_TYPED_OPENABLE)
            }
            put("CATEGORY_UNIT_TEST", Intent.CATEGORY_UNIT_TEST)
            if (VERSION.SDK_INT >= VERSION_CODES.M) {
                put("CATEGORY_VOICE", Intent.CATEGORY_VOICE)
            }
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                put("CATEGORY_VR_HOME", Intent.CATEGORY_VR_HOME)
            }
        }
    }

    fun list(): ArrayList<String> {
        initList()
        return list!!
    }

    private fun initList() {
        if (list == null) {
            list = ArrayList(CATEGORIES.keys)
            list!!.sort()
        }
    }

    fun list(keys: List<String>): List<String> {
        val list = ArrayList<String>(keys.size)
        for (key in keys) {
            list.add(CATEGORIES[key]!!)
        }
        return list
    }
}
