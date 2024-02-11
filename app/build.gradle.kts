plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("androidx.room")
}

android {
    compileSdk = 34
    namespace = "com.sdex.activityrunner"

    defaultConfig {
        applicationId = "com.activitymanager"
        minSdk = 21
        targetSdk = 34
        versionCode = 548
        versionName = "5.4.8"

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
        buildConfig = false
        viewBinding = true
        dataBinding = false
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
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.browser:browser:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    val roomVersion: String by rootProject.extra
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.vectordrawable:vectordrawable:1.1.0")
    val glideVersion = "4.16.0"
    implementation("com.github.bumptech.glide:glide:$glideVersion")
    ksp("com.github.bumptech.glide:ksp:$glideVersion")
    implementation("com.google.android.material:material:1.11.0")
    val hiltVersion: String by rootProject.extra
    implementation ("com.google.dagger:hilt-android:$hiltVersion") {
        exclude(group = "androidx.fragment", module = "fragment")
    }
    ksp("com.google.dagger:hilt-compiler:$hiltVersion")
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("com.maltaisn:icondialog:3.3.0")
    implementation("com.maltaisn:iconpack-community-material:5.3.45")
    implementation("com.simplecityapps:recyclerview-fastscroll:2.0.1")
    implementation("com.tomergoldst.android:tooltips:1.1.1")
    implementation("net.dongliu:apk-parser:2.6.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("com.github.Y-E-P:BrowserFiP:1.0.5")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.room:room-testing:$roomVersion")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
}
