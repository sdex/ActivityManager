package com.sdex.activityrunner.extensions

import android.support.v7.app.AppCompatActivity

fun AppCompatActivity.enableBackButton() {
  supportActionBar?.setDisplayHomeAsUpEnabled(true)
}