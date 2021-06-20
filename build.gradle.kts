buildscript {
    repositories {
        gradlePluginPortal()
        jcenter()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.10")
        classpath("com.android.tools.build:gradle:4.1.3")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.5.10")
        classpath("com.google.gms:google-services:4.3.8")
        classpath("com.google.firebase:perf-plugin:1.4.0")
        classpath("gradle.plugin.com.onesignal:onesignal-gradle-plugin:[0.12.10, 0.99.99]")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.5")
        classpath("org.koin:koin-gradle-plugin:2.2.2")
    }
}

group = "xyz.cleartrack"
version = "0.1-SNAPSHOT"

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        maven("https://kotlin.bintray.com/kotlinx/")
        maven("https://repo1.maven.org/maven2/")
    }
}

