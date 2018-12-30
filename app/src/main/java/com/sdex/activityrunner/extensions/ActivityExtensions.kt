package com.sdex.activityrunner.extensions

import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.enableBackButton() {
  supportActionBar?.setDisplayHomeAsUpEnabled(true)
}
