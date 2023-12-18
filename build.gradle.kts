buildscript {
    val hiltVersion by extra("2.49")
    val roomVersion by extra("2.6.1")
}

plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.dagger.hilt.android") version "2.49" apply false
    id("androidx.room") version "2.6.1" apply false
}
