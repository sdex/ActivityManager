package com.sdex.activityrunner.intent.param;

import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Action {

  private static ArrayList<String> list;
  private static final Map<String, String> ACTIONS = new HashMap<String, String>() {
    {
//      put("ACTION_AIRPLANE_MODE_CHANGED", Intent.ACTION_AIRPLANE_MODE_CHANGED);
      put("ACTION_ALL_APPS", Intent.ACTION_ALL_APPS);
      put("ACTION_ANSWER", Intent.ACTION_ANSWER);
      put("ACTION_APP_ERROR", Intent.ACTION_APP_ERROR);
      if (VERSION.SDK_INT >= VERSION_CODES.N) {
        put("ACTION_APPLICATION_PREFERENCES", Intent.ACTION_APPLICATION_PREFERENCES);
      }
//      put("ACTION_APPLICATION_RESTRICTIONS_CHANGED", Intent.ACTION_APPLICATION_RESTRICTIONS_CHANGED);
      if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
        put("ACTION_ASSIST", Intent.ACTION_ASSIST);
      }
      put("ACTION_ATTACH_DATA", Intent.ACTION_ATTACH_DATA);
//      put("ACTION_BATTERY_CHANGED", Intent.ACTION_BATTERY_CHANGED);
//      put("ACTION_BATTERY_LOW", Intent.ACTION_BATTERY_LOW);
//      put("ACTION_BATTERY_OKAY", Intent.ACTION_BATTERY_OKAY);
//      put("ACTION_BOOT_COMPLETED", Intent.ACTION_BOOT_COMPLETED);
      put("ACTION_BUG_REPORT", Intent.ACTION_BUG_REPORT);
      put("ACTION_CALL", Intent.ACTION_CALL);
      put("ACTION_CALL_BUTTON", Intent.ACTION_CALL_BUTTON);
      put("ACTION_CAMERA_BUTTON", Intent.ACTION_CAMERA_BUTTON);
      if (VERSION.SDK_INT >= VERSION_CODES.O) {
        put("ACTION_CARRIER_SETUP", Intent.ACTION_CARRIER_SETUP);
      }
      put("ACTION_CHOOSER", Intent.ACTION_CHOOSER);
      put("ACTION_CLOSE_SYSTEM_DIALOGS", Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//      put("ACTION_CONFIGURATION_CHANGED", Intent.ACTION_CONFIGURATION_CHANGED);
      if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
        put("ACTION_CREATE_DOCUMENT", Intent.ACTION_CREATE_DOCUMENT);
      }
      put("ACTION_CREATE_SHORTCUT", Intent.ACTION_CREATE_SHORTCUT);
      put("ACTION_DATE_CHANGED", Intent.ACTION_DATE_CHANGED);
      put("ACTION_DEFAULT", Intent.ACTION_DEFAULT);
      put("ACTION_DELETE", Intent.ACTION_DELETE);
      put("ACTION_DIAL", Intent.ACTION_DIAL);
      put("ACTION_DOCK_EVENT", Intent.ACTION_DOCK_EVENT);
//      put("ACTION_DREAMING_STARTED", Intent.ACTION_DREAMING_STARTED);
//      put("ACTION_DREAMING_STOPPED", Intent.ACTION_DREAMING_STOPPED);
      put("ACTION_EDIT", Intent.ACTION_EDIT);
//      put("ACTION_EXTERNAL_APPLICATIONS_AVAILABLE", Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
//      put("ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE", Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
      put("ACTION_FACTORY_TEST", Intent.ACTION_FACTORY_TEST);
      put("ACTION_GET_CONTENT", Intent.ACTION_GET_CONTENT);
      if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR2) {
        put("ACTION_GET_RESTRICTION_ENTRIES", Intent.ACTION_GET_RESTRICTION_ENTRIES);
      }
      put("ACTION_GTALK_SERVICE_CONNECTED", Intent.ACTION_GTALK_SERVICE_CONNECTED);
      put("ACTION_GTALK_SERVICE_DISCONNECTED", Intent.ACTION_GTALK_SERVICE_DISCONNECTED);
      put("ACTION_HEADSET_PLUG", Intent.ACTION_HEADSET_PLUG);
      put("ACTION_INPUT_METHOD_CHANGED", Intent.ACTION_INPUT_METHOD_CHANGED);
      put("ACTION_INSERT", Intent.ACTION_INSERT);
      put("ACTION_INSERT_OR_EDIT", Intent.ACTION_INSERT_OR_EDIT);
      if (VERSION.SDK_INT >= VERSION_CODES.O_MR1) {
        put("ACTION_INSTALL_FAILURE", Intent.ACTION_INSTALL_FAILURE);
      }
      put("ACTION_INSTALL_PACKAGE", Intent.ACTION_INSTALL_PACKAGE);
//      put("ACTION_LOCALE_CHANGED", Intent.ACTION_LOCALE_CHANGED);
//      put("ACTION_LOCKED_BOOT_COMPLETED", Intent.ACTION_LOCKED_BOOT_COMPLETED);

      put("ACTION_MAIN", Intent.ACTION_MAIN);
      put("ACTION_MANAGE_NETWORK_USAGE", Intent.ACTION_MANAGE_NETWORK_USAGE);
      put("ACTION_MANAGE_PACKAGE_STORAGE", Intent.ACTION_MANAGE_PACKAGE_STORAGE);
      if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
        put("ACTION_MANAGED_PROFILE_ADDED", Intent.ACTION_MANAGED_PROFILE_ADDED);
      }
      if (VERSION.SDK_INT >= VERSION_CODES.N) {
        put("ACTION_MANAGED_PROFILE_AVAILABLE", Intent.ACTION_MANAGED_PROFILE_AVAILABLE);
      }
      if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
        put("ACTION_MANAGED_PROFILE_REMOVED", Intent.ACTION_MANAGED_PROFILE_REMOVED);
      }
      if (VERSION.SDK_INT >= VERSION_CODES.N) {
        put("ACTION_MANAGED_PROFILE_UNAVAILABLE", Intent.ACTION_MANAGED_PROFILE_UNAVAILABLE);
      }
      if (VERSION.SDK_INT >= VERSION_CODES.N) {
        put("ACTION_MANAGED_PROFILE_UNLOCKED", Intent.ACTION_MANAGED_PROFILE_UNLOCKED);
      }
      // TODO add actions
      put("", Intent.ACTION_MEDIA_BAD_REMOVAL);
      put("", Intent.ACTION_MEDIA_BUTTON);
      put("", Intent.ACTION_MEDIA_CHECKING);
      put("", Intent.ACTION_MEDIA_EJECT);
      put("", Intent.ACTION_MEDIA_MOUNTED);
      put("", Intent.ACTION_MEDIA_NOFS);
      put("", Intent.ACTION_MEDIA_REMOVED);
      put("", Intent.ACTION_MEDIA_SCANNER_FINISHED);
      put("", Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
      put("", Intent.ACTION_MEDIA_SCANNER_STARTED);
      put("", Intent.ACTION_MEDIA_SHARED);
      put("", Intent.ACTION_MEDIA_UNMOUNTABLE);
      put("", Intent.ACTION_MEDIA_UNMOUNTED);
      put("", Intent.ACTION_MY_PACKAGE_REPLACED);
      put("", Intent.ACTION_NEW_OUTGOING_CALL);
      put("", Intent.ACTION_OPEN_DOCUMENT);
      put("", Intent.ACTION_OPEN_DOCUMENT_TREE);
      put("", Intent.ACTION_PACKAGE_ADDED);
      put("", Intent.ACTION_PACKAGE_CHANGED);
      put("", Intent.ACTION_PACKAGE_DATA_CLEARED);
      put("", Intent.ACTION_PACKAGE_FIRST_LAUNCH);
      put("", Intent.ACTION_PACKAGE_FULLY_REMOVED);
      put("", Intent.ACTION_PACKAGE_NEEDS_VERIFICATION);
      put("", Intent.ACTION_PACKAGE_REMOVED);
      put("", Intent.ACTION_PACKAGE_REPLACED);
      put("", Intent.ACTION_PACKAGE_RESTARTED);
      put("", Intent.ACTION_PACKAGE_VERIFIED);
      put("", Intent.ACTION_PACKAGES_SUSPENDED);
      put("", Intent.ACTION_PACKAGES_UNSUSPENDED);
      put("", Intent.ACTION_PACKAGE_INSTALL);
      put("", Intent.ACTION_PASTE);
      put("", Intent.ACTION_PICK);
      put("", Intent.ACTION_PICK_ACTIVITY);
      put("", Intent.ACTION_POWER_CONNECTED);
      put("", Intent.ACTION_POWER_DISCONNECTED);
      put("", Intent.ACTION_POWER_USAGE_SUMMARY);
      put("", Intent.ACTION_PROCESS_TEXT);
      put("", Intent.ACTION_PROVIDER_CHANGED);
      put("", Intent.ACTION_QUICK_CLOCK);
      put("", Intent.ACTION_QUICK_VIEW);
      put("", Intent.ACTION_REBOOT);
      put("", Intent.ACTION_RUN);
      put("", Intent.ACTION_SCREEN_OFF);
      put("", Intent.ACTION_SCREEN_ON);
      put("", Intent.ACTION_SEARCH);
      put("", Intent.ACTION_SEARCH_LONG_PRESS);
      put("", Intent.ACTION_SEND);
      put("", Intent.ACTION_SEND_MULTIPLE);
      put("", Intent.ACTION_SENDTO);
      put("", Intent.ACTION_SET_WALLPAPER);
      put("", Intent.ACTION_SHOW_APP_INFO);
      put("", Intent.ACTION_SHUTDOWN);
      put("", Intent.ACTION_SYNC);
      put("", Intent.ACTION_SYSTEM_TUTORIAL);
      put("", Intent.ACTION_TIME_CHANGED);
      put("", Intent.ACTION_TIME_TICK);
      put("", Intent.ACTION_TIMEZONE_CHANGED);
      put("", Intent.ACTION_UID_REMOVED);
      put("", Intent.ACTION_UNINSTALL_PACKAGE);
      put("", Intent.ACTION_USER_BACKGROUND);
      put("", Intent.ACTION_USER_FOREGROUND);
      put("", Intent.ACTION_USER_INITIALIZE);
      put("", Intent.ACTION_USER_PRESENT);
      put("", Intent.ACTION_USER_UNLOCKED);
      put("", Intent.ACTION_VIEW);
      put("", Intent.ACTION_VOICE_COMMAND);
      put("", Intent.ACTION_WEB_SEARCH);
      put("", Intent.ACTION_DEVICE_STORAGE_LOW);
      put("", Intent.ACTION_DEVICE_STORAGE_OK);
      put("", Intent.ACTION_UMS_CONNECTED);
      put("", Intent.ACTION_UMS_DISCONNECTED);
      put("", Intent.ACTION_WALLPAPER_CHANGED);
    }
  };

  public static ArrayList<String> list() {
    if (list == null) {
      list = new ArrayList<>(ACTIONS.keySet());
      Collections.sort(list);
      list.add(0, "NONE"); // TODO localization
    }
    return list;
  }
}
