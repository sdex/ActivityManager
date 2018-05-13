package com.sdex.activityrunner.util

import android.os.AsyncTask

class CheckRootTask(private val callback: Callback) : AsyncTask<Void, Void, Int>() {

  override fun doInBackground(params: Array<Void>): Int? {
    return if (RootUtils.isSuAvailable()) RESULT_OK else ACCESS_IS_NOT_GIVEN
  }

  override fun onPostExecute(result: Int?) {
    super.onPostExecute(result)
    this.callback.onStatusChanged(result!!)
  }

  interface Callback {

    fun onStatusChanged(status: Int)
  }

  companion object {

    const val RESULT_OK = 0
    const val ACCESS_IS_NOT_GIVEN = 1
  }
}
