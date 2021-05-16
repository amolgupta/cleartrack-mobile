import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("kotlinx-serialization")
    id("koin")
}

group = "xyz.cleartrack"
version = "0.1-SNAPSHOT"


kotlin {
    android()
    ios()
    cocoapods {
        summary = "This is sample Summary"
        homepage = "Home URL"
    }
    sourceSets {
        val commonMain by getting {
            val koinVersion = "3.0.1"
            dependencies {
                api("dev.icerock.moko:mvvm-core:0.9.2") // only ViewModel, EventsDispatcher, Dispatchers.UI

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
                implementation("io.ktor:ktor-client-cio:1.5.4")
                implementation("io.ktor:ktor-client-core:1.5.4")
                implementation("io.ktor:ktor-client-serialization:1.5.4")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0") {
                    isForce = true
                }
                implementation("com.google.code.findbugs:jsr305:3.0.2")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
                implementation("org.json:json:20201115")
                implementation("commons-lang:commons-lang:2.6")
                implementation("io.insert-koin:koin-core:$koinVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.2.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("com.google.android.material:material:1.3.0")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }

        val iosMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-ios:1.5.4")
            }
        }
    }
}

android {
    compileSdkVersion(30)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(30)
    }
}

val packForXcode by tasks.creating(Sync::class) {
    group = "build"
    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    val sdkName = System.getenv("SDK_NAME") ?: "iphonesimulator"
    val targetName = "ios" + if (sdkName.startsWith("iphoneos")) "Arm64" else "X64"
    val framework =
        kotlin.targets.getByName<KotlinNativeTarget>(targetName).binaries.getFramework(mode)
    inputs.property("mode", mode)
    dependsOn(framework.linkTask)
    val targetDir = File(buildDir, "xcode-frameworks")
    from({ framework.outputDirectory })
    into(targetDir)
}
