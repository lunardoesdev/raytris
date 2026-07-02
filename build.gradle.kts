plugins {
    id("com.android.application") version "9.2.0"
}

android {
    namespace = "retrolunar.tris"
    compileSdk = 36
    buildToolsVersion = "36.0.0"
    ndkVersion = "29.0.14206865"

    dependenciesInfo {
        // Disables dependency metadata when building APKs (for IzzyOnDroid/F-Droid)
        includeInApk = false
        // Disables dependency metadata when building Android App Bundles (for Google Play)
        includeInBundle = false
    }

    defaultConfig {
        applicationId = "retrolunar.tris"
        minSdk = 26
        targetSdk = 36
        versionCode = 2
        versionName = "1.1"
    }
    
    externalNativeBuild {
        cmake {
            path = file("CMakeLists.txt")
            version = "4.1.2"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
        }
    }

}
