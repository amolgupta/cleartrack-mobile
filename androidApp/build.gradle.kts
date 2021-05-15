plugins {
    id("com.android.application")
    id("com.onesignal.androidsdk.onesignal-gradle-plugin")
    kotlin("android")
    id("kotlin-android-extensions")
    id("androidx.navigation.safeargs.kotlin")
    id("koin")
    id("kotlin-android")
    id("com.google.gms.google-services")
    id("com.google.firebase.firebase-perf")
}

group = "xyz.cleartrack"
version = "0.1-SNAPSHOT"

dependencies {
    val koinVersion = "3.0.1"

    implementation(project(":shared"))
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")

    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("io.insert-koin:koin-core-ext:$koinVersion")
    implementation("io.insert-koin:koin-android-compat:$koinVersion")
    implementation("io.insert-koin:koin-android:$koinVersion")

    api("androidx.constraintlayout:constraintlayout:2.0.4")
    api("androidx.core:core-ktx:1.3.2")

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    api("androidx.preference:preference-ktx:1.1.1")

    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")
    implementation("androidx.security:security-crypto:1.1.0-alpha03")
    implementation("io.ktor:ktor-client-android:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.1.1")

    api("com.google.firebase:firebase-core:19.0.0")
    api("com.google.firebase:firebase-analytics:19.0.0")
    api("com.google.firebase:firebase-perf-ktx:20.0.0")
    api("com.onesignal:OneSignal:[4.0.0, 4.99.99]") {
        exclude("com.google.android")
    }
    api("com.squareup.picasso:picasso:2.71828")

    api("com.google.android.material:material:1.3.0")
    api("androidx.appcompat:appcompat:1.2.0")
    api("androidx.legacy:legacy-support-v4:1.0.0")
    api("com.github.PhilJay:MPAndroidChart:v3.1.0-alpha")
}

android {
    compileSdkVersion(30)
    defaultConfig {
        applicationId = "xyz.getclear.app"
        minSdkVersion(24)
        targetSdkVersion(30)
        versionCode = 31
        versionName = "0.112"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    packagingOptions {
        exclude("META-INF/koin-core.kotlin_module")
    }
}