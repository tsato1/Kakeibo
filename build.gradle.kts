// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val kotlin_version by extra("1.5.0")
//    val roomVersion by extra("2.3.0")
//    extra["roomVersion"] = "2.3.0"
//    val nav_version by extra("2.3.5")
//    extra["nav_version"] = "2.3.5"

    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.2.1")
        classpath("com.google.gms:google-services:4.3.8")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.6.1")
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven { url = uri("https://adcolony.bintray.com/AdColony") }
        maven { url = uri("https://jitpack.io") }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
