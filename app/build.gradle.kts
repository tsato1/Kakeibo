plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    compileSdk = 30

    val versionMajor = 5
    val versionMinor = 0
    val versionDB = 8
    val versionName = "${versionMajor}.${versionMinor}.${versionDB}"

    defaultConfig {
        applicationId = "com.kakeibo"
        minSdk = 22
        targetSdk = 30
        versionCode = 58
        /*** major.minor.db // minor:0=basic, 100=plus, 1000=plus plus ***/

        multiDexEnabled = true

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
                    "room.expandProjection" to "true"
                )
            }
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    sourceSets {
        getByName("main") {
            java.srcDir("src/main/kotlin")
        }
        getByName("androidTest") {
            java.srcDir("src/androidTestRoom_Common/java")
            assets.srcDir("$projectDir/schemas")
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            buildConfigField("int", "versionDB", "$versionDB")
            resValue("string", "versionName", "v: $versionName")
        }
        getByName("debug") {
            isDebuggable = true
            buildConfigField("int", "versionDB", "$versionDB")
            resValue("string", "versionName", "v: $versionName")
        }
    }

    lintOptions {
        disable("GoogleAppIndexingWarning")
        baseline(file("lint-baseline.xml"))
        isCheckReleaseBuilds = false
        isAbortOnError = false
    }

//    flavorDimensions "default"
//    productFlavors {
//        free {
//            buildConfigField('int', 'versionMinor', "${versionMinor}")
//        }
//        paid {
//            buildConfigField('int', 'versionMinor', "${versionMinor}+100")
//            applicationIdSuffix ".paid"
//            versionNameSuffix "-paid"
//        }
//    }

    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Android Support Libraries
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    implementation("androidx.preference:preference-ktx:1.1.1")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")
    implementation("com.google.android.material:material:1.3.0")
    implementation("com.google.android.gms:play-services-auth:19.0.0")
    implementation("com.google.android.gms:play-services-drive:17.0.0")
    implementation("com.google.android.gms:play-services-ads:20.2.0")
    implementation("android.arch.lifecycle:extensions:1.1.1")

    // Kotlin Libraries
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")

    // Room Libraries
    implementation("androidx.room:room-runtime:2.3.0")
    annotationProcessor("androidx.room:room-compiler:2.3.0")
    kapt("androidx.room:room-compiler:2.3.0")

    // Retrofit Libraries
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Billing Libraries
    implementation("com.android.billingclient:billing-ktx:4.0.0")

    // Firebase Libraries
    // These libraries versions are known to be compatible with each other.
    // If you encounter a build error "Cannot Access Hide", it means the Firebase libraries'
    // versions you are using are not compatible.
    implementation(platform("com.google.firebase:firebase-bom:27.1.0"))
    implementation("com.firebaseui:firebase-ui-auth:7.1.1")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-functions-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")

    implementation("com.google.guava:guava:27.0.1-android") // added to remove dependency error

    // Ads Libraries
    implementation("com.adcolony:sdk:4.1.0")

    // Google Drive
    implementation("com.google.http-client:google-http-client-gson:1.26.0")
    implementation("com.google.api-client:google-api-client-android:1.26.0") {
        exclude(group = "org.apache.httpcomponents")
    }
    implementation("com.google.apis:google-api-services-drive:v3-rev136-1.25.0") {
        exclude(group = "org.apache.httpcomponents")
    }

    // Other Libraries
    implementation(project(":HoloGraphLibrary"))
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.github.divyanshub024:AndroidDraw:v0.1")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.12.0")
    kapt("com.github.bumptech.glide:compiler:4.12.0")

    // Testing Libraries
//    testImplementation 'junit:junit:4.13.2'
//    androidTestImplementation "androidx.arch.core:core-testing:2.1.0"
//    androidTestImplementation 'androidx.test:runner:1.3.0'
//    androidTestImplementation 'androidx.test:core:1.3.0'
//    androidTestImplementation 'androidx.test.ext:junit:1.1.2'
//    androidTestImplementation 'androidx.test.espresso:espresso-core:3.3.0'
//    androidTestImplementation 'androidx.room:room-testing:2.2.6'
}

repositories {
    mavenCentral()
}