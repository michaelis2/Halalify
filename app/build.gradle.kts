

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


    //To use camera in the emulator
    val camerax_version="1.2.2";
    implementation ("androidx.camera:camera-core:${camerax_version}")
    implementation ("androidx.camera:camera-camera2:${camerax_version}")
    implementation ("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation ("androidx.camera:camera-video:${camerax_version}")
    implementation ("androidx.camera:camera-view:${camerax_version}")
    implementation ("androidx.camera:camera-extensions:${camerax_version}")
    implementation (libs.androidx.camera.camera2)
    implementation (libs.androidx.camera.lifecycle)
    implementation (libs.androidx.camera.view.v140)
    implementation(libs.androidx.camera.view)

    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    //firebase
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.bom)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation (libs.firebase.ui.auth)
    implementation (libs.firebase.firestore)
    implementation (libs.firebase.storage)

    //barcode
    implementation (libs.barcode.scanning)
    implementation (libs.androidx.lifecycle.runtime.ktx)
    implementation (libs.barcode.scanning.v1700)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.logging.interceptor)
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")

    //image
    implementation (libs.glide)
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")

    //testings
    testImplementation("io.mockk:mockk:1.13.13")
    testImplementation(libs.junit)
    testImplementation(libs.androidx.junit)
    testImplementation(libs.androidx.espresso.core)
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation ("org.robolectric:robolectric:4.10.3")
    testImplementation ("androidx.test.espresso:espresso-intents:3.5.1")
    testImplementation ("androidx.test.espresso:espresso-contrib:3.5.1")
    testImplementation ("androidx.test.espresso:espresso-accessibility:3.5.1")
    debugImplementation ("androidx.fragment:fragment-testing:1.5.7")

    //instrumentation testings
    androidTestImplementation("io.mockk:mockk-android:1.13.13")
    androidTestImplementation (libs.androidx.espresso.core.v340)
    androidTestImplementation ("androidx.test.ext:junit:1.1.3")
    androidTestImplementation ("androidx.test:rules:1.6.1")
    androidTestImplementation (libs.androidx.camera.camera.core)
    androidTestImplementation  (libs.camera.camera2.v122)
    androidTestImplementation  (libs.camera.lifecycle.v122)
    androidTestImplementation  (libs.androidx.camera.camera.video)
    androidTestImplementation  (libs.camera.view.v122)
    androidTestImplementation  (libs.camera.extensions)  // For testing CameraX
    androidTestImplementation (libs.mockito.core)
    androidTestImplementation (libs.mockito.kotlin)
    androidTestImplementation (libs.barcode.scanning)
    androidTestImplementation (libs.google.firebase.analytics)
    androidTestImplementation ("org.jetbrains.kotlin:kotlin-stdlib:1.9.0") // Replace with the latest version if necessary
}
