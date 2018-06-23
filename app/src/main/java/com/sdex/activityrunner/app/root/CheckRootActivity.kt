package com.sdex.activityrunner.app.root

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.sdex.activityrunner.R
import com.sdex.activityrunner.extensions.enableBackButton
import com.sdex.activityrunner.util.CheckRootTask
import com.sdex.commons.BaseActivity
import kotlinx.android.synthetic.main.activity_check_root.*

class CheckRootActivity : BaseActivity() {

  override fun getLayout(): Int {
    return R.layout.activity_check_root
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableBackButton()

    check.setOnClickListener {
      checkRoot()
    }
  }

  private fun checkRoot() {
    val checkRootTask = CheckRootTask(object : CheckRootTask.Callback {
      override fun onStatusChanged(status: Int) {
        if (!isFinishing) {
          if (status == CheckRootTask.RESULT_OK) {
            statusView.setText(R.string.root_check_compatibility_status_ok)
          } else {
            statusView.setText(R.string.root_check_compatibility_status_fail)
          }
        }
      }
    })
    checkRootTask.execute()
  }

  companion object {

    fun start(context: Context) {
      val starter = Intent(context, CheckRootActivity::class.java)
      context.startActivity(starter)
    }
  }
}