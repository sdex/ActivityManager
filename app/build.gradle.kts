plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinCompiler)
    alias(libs.plugins.kotlinParcelize)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.room)
}

// TODO: Remove when Dagger bumps Kotlin metadata dependency to 2.4.0
configurations.configureEach {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin" && requested.name == "kotlin-metadata-jvm") {
            // Hilt 2.59.2 reads Kotlin metadata through this library and does not yet
            // declare a version that can parse Kotlin 2.4 metadata.
            useVersion(libs.versions.kotlin.get())
        }
    }
}

android {
    compileSdk = 37
    namespace = "com.sdex.activityrunner"

    defaultConfig {
        applicationId = "com.activitymanager"
        minSdk = 23
        targetSdk = 36
        versionCode = 563
        versionName = "5.4.23"

        project.findProperty("newVersionCode")?.toString()?.toIntOrNull()?.let {
            versionCode = it
        }
        project.findProperty("versionNameSuffix")?.toString()?.let {
            versionName = "${versionName}${it}-$versionCode"
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables.useSupportLibrary = true
        base.archivesName.set("ActivityManager-$versionName")
    }

    androidResources {
        generateLocaleConfig = true
    }

    flavorDimensions.add("type")

    productFlavors {
        create("dev") {
            dimension = "type"
            applicationIdSuffix = ".dev"
        }

        create("prod") {
            dimension = "type"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    sourceSets {
        getByName("androidTest").assets.directories.add("schemas")
    }

    lint {
        abortOnError = true
        baseline = file("lint-baseline.xml")
        lintConfig = file("lint.xml")
    }
}

kotlin {
    jvmToolchain(17)
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.appcompat)
    implementation(libs.browser)
    implementation(libs.constraintlayout)
    implementation(libs.core.ktx)
    implementation(libs.fragment.ktx)
    implementation(libs.preference.ktx)
    implementation(libs.recyclerview)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.vectordrawable)
    implementation(libs.glide)
    ksp(libs.glide.ksp)
    implementation(libs.material)
    implementation(libs.hilt.android) {
        exclude(group = "androidx.fragment", module = "fragment")
    }
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)
    implementation(libs.timber)
    implementation(libs.icondialog)
    implementation(libs.iconpack.community.material)
    implementation(libs.recyclerview.fastscroll)
    implementation(libs.apk.parser)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.datastore.preferences)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.browserfip)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.tv.foundation)
    implementation(libs.androidx.tv.material)
    implementation(libs.androidx.activity.compose)
    implementation(libs.navigation3.runtime)
    implementation(libs.navigation3.ui)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.viewmodel.navigation3)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.coil.compose)
    implementation(libs.appiconloader.coil)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.truth)

    androidTestImplementation(libs.room.testing)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.espresso.intents)
}
