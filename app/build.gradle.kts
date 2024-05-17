import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.aboutlibraries)
}

kotlin {
    jvmToolchain(17)
}

android {
    namespace = "com.afzaln.awakedebug"
    compileSdk = 34

    defaultConfig {
        minSdk = 27
        targetSdk = 34
        versionCode = 13
        versionName = "2.1"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    signingConfigs {
        create("release") {
            val keyProps = Properties()
            val keyProperties = file("../keystore.roperties")
            if (keyProperties.exists()) {
                // when building locally, use signing properties from keystore.properties
                keyProps.load(FileInputStream(keyProperties))
            } else {
                // when building CI server, use signing properties from environment variables
                keyProps.setProperty("storePass", System.getenv("RELEASE_KEYSTORE_PASSWORD") ?: "")
                keyProps.setProperty("pass", System.getenv("RELEASE_KEYSTORE_PASSWORD") ?: "")
                keyProps.setProperty("alias", System.getenv("RELEASE_KEY_ALIAS") ?: "")
                keyProps.setProperty("store", "../awakedebug.keystore")
            }

            storeFile = file(keyProps["store"]!!)
            keyAlias = keyProps["alias"] as String
            storePassword = keyProps ["storePass"] as String
            keyPassword = keyProps["pass"] as String
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.txt")
            signingConfig = signingConfigs.getByName("release")
        }
    }

    packaging {
        resources.excludes += "DebugProbesKt.bin"
    }
}

dependencies {
    implementation("com.google.android.material:material:1.2.1")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.jakewharton.timber:timber:4.7.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")
    implementation("androidx.activity:activity-ktx:1.1.0")
    implementation("androidx.preference:preference-ktx:1.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.1")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation(libs.aboutlibraries)

    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.14")
}
