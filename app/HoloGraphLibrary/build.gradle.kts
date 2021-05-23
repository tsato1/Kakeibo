plugins {
    id("com.android.library")
    id("kotlin-android")
}

buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.2.1")
    }
}

repositories {
    mavenCentral()
}

android {
    compileSdk = 30

    defaultConfig {
        minSdk = 17
        targetSdk = 30
    }

    sourceSets {
        getByName("main") {
            manifest.srcFile("AndroidManifest.xml")
            java.srcDir("src")
            resources.srcDirs("src")
            aidl.srcDirs("src")
            renderscript.srcDirs("src")
            res.srcDirs("res")
            assets.srcDirs("assets")
        }
    }
}

dependencies {
}