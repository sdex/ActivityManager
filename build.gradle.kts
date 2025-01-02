buildscript {
    val hiltVersion by extra("2.54") // update the plugin version as well
    val roomVersion by extra("2.6.1") // update the plugin version as well
}

plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
    id("com.google.dagger.hilt.android") version "2.54" apply false
    id("androidx.room") version "2.6.1" apply false
}
