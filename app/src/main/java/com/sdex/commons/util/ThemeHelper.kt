package com.sdex.commons.util

import android.app.Activity
import com.sdex.activityrunner.R

class ThemeHelper {

  fun setTheme(activity: Activity, theme: String?) {
    theme.let {
      val style = when (theme) {
        "0" -> R.style.AppTheme_Light
        "1" -> R.style.AppTheme_Dark
        else -> R.style.AppTheme_Light
      }
      activity.setTheme(style)
    }
  }

  fun setDialogTheme(activity: Activity, theme: String?) {
    theme.let {
      val style = when (theme) {
        "0" -> R.style.AppDialogTheme_Light
        "1" -> R.style.AppDialogTheme_Dark
        else -> R.style.AppDialogTheme_Light
      }
      activity.setTheme(style)
    }
  }
}
