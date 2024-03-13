package com.sdex.activityrunner.intent.param

import android.content.Intent
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES

object Action {

    private var list: ArrayList<String>? = null
    private val ACTIONS = object : HashMap<String, String>() {
        init {
            //      put("ACTION_AIRPLANE_MODE_CHANGED", Intent.ACTION_AIRPLANE_MODE_CHANGED);
            put("ACTION_ALL_APPS", Intent.ACTION_ALL_APPS)
            put("ACTION_ANSWER", Intent.ACTION_ANSWER)
            put("ACTION_APP_ERROR", Intent.ACTION_APP_ERROR)
            if (VERSION.SDK_INT >= VERSION_CODES.N) {
                put("ACTION_APPLICATION_PREFERENCES", Intent.ACTION_APPLICATION_PREFERENCES)
            }
            //      put("ACTION_APPLICATION_RESTRICTIONS_CHANGED", Intent.ACTION_APPLICATION_RESTRICTIONS_CHANGED);
            put("ACTION_ASSIST", Intent.ACTION_ASSIST)
            put("ACTION_ATTACH_DATA", Intent.ACTION_ATTACH_DATA)
            //      put("ACTION_BATTERY_CHANGED", Intent.ACTION_BATTERY_CHANGED);
            //      put("ACTION_BATTERY_LOW", Intent.ACTION_BATTERY_LOW);
            //      put("ACTION_BATTERY_OKAY", Intent.ACTION_BATTERY_OKAY);
            //      put("ACTION_BOOT_COMPLETED", Intent.ACTION_BOOT_COMPLETED);
            put("ACTION_BUG_REPORT", Intent.ACTION_BUG_REPORT)
            put("ACTION_CALL", Intent.ACTION_CALL)
            put("ACTION_CALL_BUTTON", Intent.ACTION_CALL_BUTTON)
            //      put("ACTION_CAMERA_BUTTON", Intent.ACTION_CAMERA_BUTTON); // Broadcast Action
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                put("ACTION_CARRIER_SETUP", Intent.ACTION_CARRIER_SETUP)
            }
            put("ACTION_CHOOSER", Intent.ACTION_CHOOSER)
            //      put("ACTION_CLOSE_SYSTEM_DIALOGS", Intent.ACTION_CLOSE_SYSTEM_DIALOGS); // Broadcast Action
            //      put("ACTION_CONFIGURATION_CHANGED", Intent.ACTION_CONFIGURATION_CHANGED);
            put("ACTION_CREATE_DOCUMENT", Intent.ACTION_CREATE_DOCUMENT)
            put("ACTION_CREATE_SHORTCUT", Intent.ACTION_CREATE_SHORTCUT)
            //      put("ACTION_DATE_CHANGED", Intent.ACTION_DATE_CHANGED); // Broadcast Action
            //      put("ACTION_DEFAULT", Intent.ACTION_DEFAULT); // ACTION_VIEW the same
            put("ACTION_DELETE", Intent.ACTION_DELETE)
            put("ACTION_DIAL", Intent.ACTION_DIAL)
            //      put("ACTION_DOCK_EVENT", Intent.ACTION_DOCK_EVENT); // Broadcast Action
            //      put("ACTION_DREAMING_STARTED", Intent.ACTION_DREAMING_STARTED);
            //      put("ACTION_DREAMING_STOPPED", Intent.ACTION_DREAMING_STOPPED);
            put("ACTION_EDIT", Intent.ACTION_EDIT)
            //      put("ACTION_EXTERNAL_APPLICATIONS_AVAILABLE", Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
            //      put("ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE", Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
            put("ACTION_FACTORY_TEST", Intent.ACTION_FACTORY_TEST)
            put("ACTION_GET_CONTENT", Intent.ACTION_GET_CONTENT)
            //      if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR2) {
            //        put("ACTION_GET_RESTRICTION_ENTRIES", Intent.ACTION_GET_RESTRICTION_ENTRIES); // Broadcast Action
            //      }
            //      put("ACTION_GTALK_SERVICE_CONNECTED", Intent.ACTION_GTALK_SERVICE_CONNECTED); // Broadcast Action
            //      put("ACTION_GTALK_SERVICE_DISCONNECTED", Intent.ACTION_GTALK_SERVICE_DISCONNECTED); // Broadcast Action
            //      put("ACTION_HEADSET_PLUG", Intent.ACTION_HEADSET_PLUG); // Broadcast Action
            //      put("ACTION_INPUT_METHOD_CHANGED", Intent.ACTION_INPUT_METHOD_CHANGED); // Broadcast Action
            put("ACTION_INSERT", Intent.ACTION_INSERT)
            put("ACTION_INSERT_OR_EDIT", Intent.ACTION_INSERT_OR_EDIT)
            if (VERSION.SDK_INT >= VERSION_CODES.O_MR1) {
                put("ACTION_INSTALL_FAILURE", Intent.ACTION_INSTALL_FAILURE)
            }
            @Suppress("DEPRECATION")
            put("ACTION_INSTALL_PACKAGE", Intent.ACTION_INSTALL_PACKAGE)
            //      put("ACTION_LOCALE_CHANGED", Intent.ACTION_LOCALE_CHANGED);
            //      put("ACTION_LOCKED_BOOT_COMPLETED", Intent.ACTION_LOCKED_BOOT_COMPLETED);

            put("ACTION_MAIN", Intent.ACTION_MAIN)
            put("ACTION_MANAGE_NETWORK_USAGE", Intent.ACTION_MANAGE_NETWORK_USAGE)
            //      put("ACTION_MANAGE_PACKAGE_STORAGE", Intent.ACTION_MANAGE_PACKAGE_STORAGE); // Broadcast Action
            //      if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            //        put("ACTION_MANAGED_PROFILE_ADDED", Intent.ACTION_MANAGED_PROFILE_ADDED); // Broadcast Action
            //      }
            //      if (VERSION.SDK_INT >= VERSION_CODES.N) {
            //        put("ACTION_MANAGED_PROFILE_AVAILABLE", Intent.ACTION_MANAGED_PROFILE_AVAILABLE); // Broadcast Action
            //      }
            //      if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            //        put("ACTION_MANAGED_PROFILE_REMOVED", Intent.ACTION_MANAGED_PROFILE_REMOVED); // Broadcast Action
            //      }
            //      if (VERSION.SDK_INT >= VERSION_CODES.N) {
            //        put("ACTION_MANAGED_PROFILE_UNAVAILABLE", Intent.ACTION_MANAGED_PROFILE_UNAVAILABLE); // Broadcast Action
            //      }
            //      if (VERSION.SDK_INT >= VERSION_CODES.N) {
            //        put("ACTION_MANAGED_PROFILE_UNLOCKED", Intent.ACTION_MANAGED_PROFILE_UNLOCKED); // Broadcast Action
            //      }
            // Broadcast Action
            //      put("ACTION_MEDIA_BAD_REMOVAL", Intent.ACTION_MEDIA_BAD_REMOVAL);
            //      put("ACTION_MEDIA_BUTTON", Intent.ACTION_MEDIA_BUTTON);
            //      put("ACTION_MEDIA_CHECKING", Intent.ACTION_MEDIA_CHECKING);
            //      put("ACTION_MEDIA_EJECT", Intent.ACTION_MEDIA_EJECT);
            //      put("ACTION_MEDIA_MOUNTED", Intent.ACTION_MEDIA_MOUNTED);
            //      put("ACTION_MEDIA_NOFS", Intent.ACTION_MEDIA_NOFS);
            //      put("ACTION_MEDIA_REMOVED", Intent.ACTION_MEDIA_REMOVED);
            //      put("ACTION_MEDIA_SCANNER_FINISHED", Intent.ACTION_MEDIA_SCANNER_FINISHED);
            //      put("ACTION_MEDIA_SCANNER_SCAN_FILE", Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            //      put("ACTION_MEDIA_SCANNER_STARTED", Intent.ACTION_MEDIA_SCANNER_STARTED);
            //      put("ACTION_MEDIA_SHARED", Intent.ACTION_MEDIA_SHARED);
            //      put("ACTION_MEDIA_UNMOUNTABLE", Intent.ACTION_MEDIA_UNMOUNTABLE);
            //      put("ACTION_MEDIA_UNMOUNTED", Intent.ACTION_MEDIA_UNMOUNTED);
            //      put("ACTION_MY_PACKAGE_REPLACED", Intent.ACTION_MY_PACKAGE_REPLACED);
            //      put("ACTION_NEW_OUTGOING_CALL", Intent.ACTION_NEW_OUTGOING_CALL);
            // END  // Broadcast Action
            put("ACTION_OPEN_DOCUMENT", Intent.ACTION_OPEN_DOCUMENT)
            put("ACTION_OPEN_DOCUMENT_TREE", Intent.ACTION_OPEN_DOCUMENT_TREE)
            //      put("ACTION_PACKAGE_ADDED", Intent.ACTION_PACKAGE_ADDED);
            //      put("ACTION_PACKAGE_CHANGED", Intent.ACTION_PACKAGE_CHANGED);
            //      put("ACTION_PACKAGE_DATA_CLEARED", Intent.ACTION_PACKAGE_DATA_CLEARED);
            //      put("ACTION_PACKAGE_FIRST_LAUNCH", Intent.ACTION_PACKAGE_FIRST_LAUNCH);
            //      put("ACTION_PACKAGE_FULLY_REMOVED", Intent.ACTION_PACKAGE_FULLY_REMOVED);
            //      put("ACTION_PACKAGE_NEEDS_VERIFICATION", Intent.ACTION_PACKAGE_NEEDS_VERIFICATION);
            //      put("ACTION_PACKAGE_REMOVED", Intent.ACTION_PACKAGE_REMOVED);
            //      put("ACTION_PACKAGE_REPLACED", Intent.ACTION_PACKAGE_REPLACED);
            //      put("ACTION_PACKAGE_RESTARTED", Intent.ACTION_PACKAGE_RESTARTED);
            //      put("ACTION_PACKAGE_VERIFIED", Intent.ACTION_PACKAGE_VERIFIED);
            //      put("ACTION_PACKAGES_SUSPENDED", Intent.ACTION_PACKAGES_SUSPENDED);
            //      put("ACTION_PACKAGES_UNSUSPENDED", Intent.ACTION_PACKAGES_UNSUSPENDED);
            //      put("ACTION_PACKAGE_INSTALL", Intent.ACTION_PACKAGE_INSTALL);
            put("ACTION_PASTE", Intent.ACTION_PASTE)
            put("ACTION_PICK", Intent.ACTION_PICK)
            put("ACTION_PICK_ACTIVITY", Intent.ACTION_PICK_ACTIVITY)
            //      put("ACTION_POWER_CONNECTED", Intent.ACTION_POWER_CONNECTED);
            //      put("ACTION_POWER_DISCONNECTED", Intent.ACTION_POWER_DISCONNECTED);
            put("ACTION_POWER_USAGE_SUMMARY", Intent.ACTION_POWER_USAGE_SUMMARY)
            if (VERSION.SDK_INT >= VERSION_CODES.M) {
                put("ACTION_PROCESS_TEXT", Intent.ACTION_PROCESS_TEXT)
            }
            //      put("ACTION_PROVIDER_CHANGED", Intent.ACTION_PROVIDER_CHANGED); // Broadcast Action
            put("ACTION_QUICK_CLOCK", Intent.ACTION_QUICK_CLOCK)
            if (VERSION.SDK_INT >= VERSION_CODES.N) {
                put("ACTION_QUICK_VIEW", Intent.ACTION_QUICK_VIEW)
            }
            //      put("ACTION_REBOOT", Intent.ACTION_REBOOT);
            put("ACTION_RUN", Intent.ACTION_RUN)
            //      put("ACTION_SCREEN_OFF", Intent.ACTION_SCREEN_OFF);
            //      put("ACTION_SCREEN_ON", Intent.ACTION_SCREEN_ON);
            put("ACTION_SEARCH", Intent.ACTION_SEARCH)
            put("ACTION_SEARCH_LONG_PRESS", Intent.ACTION_SEARCH_LONG_PRESS)
            put("ACTION_SEND", Intent.ACTION_SEND)
            put("ACTION_SEND_MULTIPLE", Intent.ACTION_SEND_MULTIPLE)
            put("ACTION_SENDTO", Intent.ACTION_SENDTO)
            put("ACTION_SET_WALLPAPER", Intent.ACTION_SET_WALLPAPER)
            if (VERSION.SDK_INT >= VERSION_CODES.N) {
                put("ACTION_SHOW_APP_INFO", Intent.ACTION_SHOW_APP_INFO)
            }
            //      put("ACTION_SHUTDOWN", Intent.ACTION_SHUTDOWN);
            put("ACTION_SYNC", Intent.ACTION_SYNC)
            put("ACTION_SYSTEM_TUTORIAL", Intent.ACTION_SYSTEM_TUTORIAL)
            //      put("ACTION_TIME_CHANGED", Intent.ACTION_TIME_CHANGED); // Broadcast Action
            //      put("ACTION_TIME_TICK", Intent.ACTION_TIME_TICK);
            //      put("ACTION_TIMEZONE_CHANGED", Intent.ACTION_TIMEZONE_CHANGED);
            //      put("ACTION_UID_REMOVED", Intent.ACTION_UID_REMOVED);
            @Suppress("DEPRECATION")
            put("ACTION_UNINSTALL_PACKAGE", Intent.ACTION_UNINSTALL_PACKAGE)
            //      if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
            //        put("ACTION_USER_BACKGROUND", Intent.ACTION_USER_BACKGROUND); // Broadcast Action
            //      }
            //      if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
            //        put("ACTION_USER_FOREGROUND", Intent.ACTION_USER_FOREGROUND); // Broadcast Action
            //      }
            //      if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
            //        put("ACTION_USER_INITIALIZE", Intent.ACTION_USER_INITIALIZE);
            //      }
            //      put("ACTION_USER_PRESENT", Intent.ACTION_USER_PRESENT);
            //      if (VERSION.SDK_INT >= VERSION_CODES.N) {
            //        put("ACTION_USER_UNLOCKED", Intent.ACTION_USER_UNLOCKED); // Broadcast Action
            //      }
            put("ACTION_VIEW", Intent.ACTION_VIEW)
            put("ACTION_VOICE_COMMAND", Intent.ACTION_VOICE_COMMAND)
            put("ACTION_WEB_SEARCH", Intent.ACTION_WEB_SEARCH)
            //      put("ACTION_DEVICE_STORAGE_LOW", Intent.ACTION_DEVICE_STORAGE_LOW);
            //      put("ACTION_DEVICE_STORAGE_OK", Intent.ACTION_DEVICE_STORAGE_OK);
            //      put("ACTION_UMS_CONNECTED", Intent.ACTION_UMS_CONNECTED); // Broadcast Action
            //      put("ACTION_UMS_DISCONNECTED", Intent.ACTION_UMS_DISCONNECTED); // Broadcast Action
            //      put("ACTION_WALLPAPER_CHANGED", Intent.ACTION_WALLPAPER_CHANGED); // Broadcast Action
        }
    }

    private fun initList() {
        if (list == null) {
            list = ArrayList(ACTIONS.keys)
            list!!.sort()
            list!!.add(0, None.VALUE)
        }
    }

    fun list(): ArrayList<String> {
        initList()
        return list!!
    }

    fun getAction(key: String): String? {
        return ACTIONS[key]
    }

    fun getActionKeyPosition(value: String): Int {
        val split = value.split(".")
        val last = split.lastOrNull() ?: return -1
        initList()
        for (action in list!!) {
            if (action.contains(last)) {
                return list!!.indexOf(action)
            }
        }
        return -1
    }
}
