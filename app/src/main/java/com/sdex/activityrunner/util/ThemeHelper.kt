package com.sdex.activityrunner.util

import android.app.Activity
import com.sdex.activityrunner.R
import com.sdex.highlightjs.models.Theme

class ThemeHelper {

  fun setTheme(activity: Activity, theme: String?, isBlack: Boolean) {
    theme.let {
      val style = when (theme?.toInt()) {
        LIGHT_THEME -> R.style.AppTheme_Light
        DARK_THEME -> {
          if (isBlack) {
            R.style.AppTheme_Dark_Black
          } else {
            R.style.AppTheme_Dark
          }
        }
        else -> R.style.AppTheme_Light
      }
      activity.setTheme(style)
    }
  }

  fun setDialogTheme(activity: Activity, theme: String?) {
    theme.let {
      val style = when (theme?.toInt()) {
        LIGHT_THEME -> R.style.AppDialogTheme_Light
        DARK_THEME -> R.style.AppDialogTheme_Dark
        else -> R.style.AppDialogTheme_Light
      }
      activity.setTheme(style)
    }
  }

  fun getWebViewTheme(theme: String?): Theme {
    theme.let {
      return when (theme?.toInt()) {
        LIGHT_THEME -> Theme.GITHUB_GIST
        DARK_THEME -> Theme.DARKULA
        else -> Theme.GITHUB_GIST
      }
    }
  }

  companion object {
    const val LIGHT_THEME = 0
    const val DARK_THEME = 1
  }
}
