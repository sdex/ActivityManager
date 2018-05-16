package com.sdex.activityrunner.util

import android.content.Context
import android.content.res.Configuration

object Utils {

  fun isXLargeTablet(context: Context): Boolean {
    return context.resources.configuration.screenLayout and
      Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE
  }
}
