package com.sdex.activityrunner.ui

import android.app.Activity
import android.view.View

interface SnackbarContainerActivity {

  fun getView(): View

  fun getActivity(): Activity
}
