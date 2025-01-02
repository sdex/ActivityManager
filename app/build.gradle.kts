plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.kotlinParcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.room)
}

android {
    compileSdk = 34
    namespace = "com.sdex.activityrunner"

    defaultConfig {
        applicationId = "com.activitymanager"
        minSdk = 21
        targetSdk = 34
        versionCode = 551
        versionName = "5.4.11"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables.useSupportLibrary = true
        multiDexEnabled = true
        base.archivesBaseName = "ActivityManager-$versionName"
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

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    buildFeatures {
        viewBinding = true
    }

    sourceSets {
        getByName("androidTest").assets.srcDir("$projectDir/schemas")
    }

    lint {
        abortOnError = true
        baseline = file("lint-baseline.xml")
        lintConfig = file("lint.xml")
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.browser)
    implementation(libs.constraintlayout)
    implementation(libs.core.ktx)
    implementation(libs.fragment.ktx)
    implementation(libs.multidex)
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
    ksp(libs.hilt.compiler)
    implementation(libs.timber)
    implementation(libs.icondialog)
    implementation(libs.iconpack.community.material)
    implementation(libs.recyclerview.fastscroll)
    implementation(libs.tooltips)
    implementation(libs.apk.parser)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.browserfip)

    testImplementation(libs.junit)

    androidTestImplementation(libs.room.testing)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.espresso.intents)
}
