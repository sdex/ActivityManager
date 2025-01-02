buildscript {
    val hiltVersion by extra("2.52") // update the plugin version as well
    val roomVersion by extra("2.6.1") // update the plugin version as well
}

plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
    id("com.google.devtools.ksp") version "1.9.24-1.0.20" apply false
    id("com.google.dagger.hilt.android") version "2.52" apply false
    id("androidx.room") version "2.6.1" apply false
}
