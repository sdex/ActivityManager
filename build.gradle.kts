buildscript {
    val hiltVersion by extra("2.50") // update the plugin version as well
    val roomVersion by extra("2.6.1") // update the plugin version as well
}

plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
    id("androidx.room") version "2.6.1" apply false
}
