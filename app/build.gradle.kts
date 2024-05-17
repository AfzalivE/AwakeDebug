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
        versionCode = 14
        versionName = "2.2"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    signingConfigs {
        create("release") {
            val keyProps = Properties()
            val keyProperties = file("../keystore.properties")
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
    implementation(libs.material)
    implementation(libs.appcompat)
    implementation(libs.timber)
    implementation(libs.activity.ktx)
    implementation(libs.preference.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.constraintlayout)
    implementation(libs.aboutlibraries)

    debugImplementation(libs.leakcanary.android)
}
