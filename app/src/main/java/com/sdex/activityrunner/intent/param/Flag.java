package com.sdex.activityrunner.intent.param;

import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Flag {

  private static ArrayList<String> list;
  private static final Map<String, Integer> FLAGS = new HashMap<String, Integer>() {
    {
      put("FLAG_ACTIVITY_BROUGHT_TO_FRONT", Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
      put("FLAG_ACTIVITY_CLEAR_TASK", Intent.FLAG_ACTIVITY_CLEAR_TASK);
      put("FLAG_ACTIVITY_CLEAR_TOP", Intent.FLAG_ACTIVITY_CLEAR_TOP);
      put("FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS", Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
      put("FLAG_ACTIVITY_FORWARD_RESULT", Intent.FLAG_ACTIVITY_FORWARD_RESULT);
      if (VERSION.SDK_INT >= VERSION_CODES.N) {
        put("FLAG_ACTIVITY_LAUNCH_ADJACENT", Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT);
      }
      put("FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY", Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
      put("FLAG_ACTIVITY_MULTIPLE_TASK", Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
      if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
        put("FLAG_ACTIVITY_NEW_DOCUMENT", Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
      }
      put("FLAG_ACTIVITY_NEW_TASK", Intent.FLAG_ACTIVITY_NEW_TASK);
      put("FLAG_ACTIVITY_NO_ANIMATION", Intent.FLAG_ACTIVITY_NO_ANIMATION);
      put("FLAG_ACTIVITY_NO_HISTORY", Intent.FLAG_ACTIVITY_NO_HISTORY);
      put("FLAG_ACTIVITY_NO_USER_ACTION", Intent.FLAG_ACTIVITY_NO_USER_ACTION);
      put("FLAG_ACTIVITY_PREVIOUS_IS_TOP", Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
      put("FLAG_ACTIVITY_REORDER_TO_FRONT", Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
      put("FLAG_ACTIVITY_RESET_TASK_IF_NEEDED", Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
      if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
        put("FLAG_ACTIVITY_RETAIN_IN_RECENTS", Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS);
      }
      put("FLAG_ACTIVITY_SINGLE_TOP", Intent.FLAG_ACTIVITY_SINGLE_TOP);
      put("FLAG_ACTIVITY_TASK_ON_HOME", Intent.FLAG_ACTIVITY_TASK_ON_HOME);
      // TODO add flags
    }
  };

  public static ArrayList<String> list() {
    if (list == null) {
      list = new ArrayList<>(FLAGS.keySet());
      Collections.sort(list);
    }
    return list;
  }

  public static List<Integer> list(List<String> keys) {
    List<Integer> list = new ArrayList<>(keys.size());
    for (String key : keys) {
      list.add(FLAGS.get(key));
    }
    return list;
  }
}
