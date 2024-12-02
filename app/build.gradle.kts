

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.halalify"
    compileSdk = 34
    packagingOptions {
        resources {
            excludes += setOf(
                "META-INF/LICENSE-notice.md",
                "META-INF/LICENSE.md",
                "META-INF/LICENSE",
                "META-INF/NOTICE",
                "META-INF/NOTICE.md",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE.txt"
            )
        }
    }


    defaultConfig {
        applicationId = "com.example.halalify"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
}


dependencies {

    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.junit.ktx)
    val camerax_version="1.2.2";
    implementation ("androidx.camera:camera-core:${camerax_version}")
    implementation ("androidx.camera:camera-camera2:${camerax_version}")
    implementation ("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation ("androidx.camera:camera-video:${camerax_version}")

    implementation ("androidx.camera:camera-view:${camerax_version}")
    implementation ("androidx.camera:camera-extensions:${camerax_version}")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.camera.view)
    implementation(libs.firebase.auth.ktx)
    testImplementation(libs.junit)
   testImplementation(libs.androidx.junit)
    testImplementation(libs.androidx.espresso.core)
    implementation(libs.firebase.bom)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation (libs.firebase.ui.auth)
    implementation (libs.firebase.firestore)
    implementation (libs.firebase.storage)
    implementation (libs.androidx.camera.camera2)
    implementation (libs.androidx.camera.lifecycle)
    implementation (libs.androidx.camera.view.v140)
    implementation (libs.barcode.scanning)
    implementation (libs.androidx.lifecycle.runtime.ktx)
    implementation (libs.barcode.scanning.v1700)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.logging.interceptor)

    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation (libs.glide)
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1") // If using annotation processing (optional)
    testImplementation("io.mockk:mockk:1.13.13")
    androidTestImplementation("io.mockk:mockk-android:1.13.13") // For instrumented tests
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
  //  androidTestImplementation ("org.robolectric:robolectric:4.9.2")  // Robolectric for instrumented tests
    testImplementation ("org.robolectric:robolectric:4.10.3")
    // Espresso-Intents for testing Intents (optional, if needed)
   testImplementation ("androidx.test.espresso:espresso-intents:3.5.1")

    // Espresso-Contrib for extra matchers and actions (optional)
   testImplementation ("androidx.test.espresso:espresso-contrib:3.5.1")

    // Espresso for testing accessibility (optional)
  testImplementation ("androidx.test.espresso:espresso-accessibility:3.5.1")
    debugImplementation ("androidx.fragment:fragment-testing:1.5.7")


    androidTestImplementation ("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation ("androidx.test.ext:junit:1.1.3")
    androidTestImplementation ("androidx.test:rules:1.4.0")
    androidTestImplementation ("androidx.camera:camera-core:${camerax_version}")
    androidTestImplementation  ("androidx.camera:camera-camera2:${camerax_version}")
    androidTestImplementation  ("androidx.camera:camera-lifecycle:${camerax_version}")
    androidTestImplementation  ("androidx.camera:camera-video:${camerax_version}")

    androidTestImplementation  ("androidx.camera:camera-view:${camerax_version}")
    androidTestImplementation  ("androidx.camera:camera-extensions:${camerax_version}")  // For testing CameraX
    androidTestImplementation ("org.mockito:mockito-core:5.4.0")
    androidTestImplementation ("org.mockito.kotlin:mockito-kotlin:5.4.0")
    androidTestImplementation (libs.barcode.scanning)
    androidTestImplementation ("com.google.firebase:firebase-analytics:21.0.0")
    androidTestImplementation ("org.jetbrains.kotlin:kotlin-stdlib:1.8.10") // Replace with the latest version if necessary
}
