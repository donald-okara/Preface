plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.don.bookish"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.don.bookish"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    //Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.51")
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.palette.ktx)
    kapt("com.google.dagger:hilt-android-compiler:2.51")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0") // Replace with the latest versionimplementation("androidx.hilt:hilt-lifecycle-viewmodel:1.1.0") // Replace with the latest version
    kapt("androidx.hilt:hilt-compiler:1.1.0") // Or use ksp

    // Retrofit with Kotlin serialization Converter
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("androidx.palette:palette:1.0.0")


    //Coil
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("io.coil-kt:coil:2.0.0-rc02")
    implementation("io.coil-kt:coil-gif:2.0.0-rc02")
    implementation("io.coil-kt:coil-compose:2.0.0-rc02")
    implementation("androidx.core:core-splashscreen:1.0.0")


    //Html parser
    implementation("androidx.core:core-ktx:1.9.0") // Check for the latest version


    implementation("com.github.bumptech.glide:glide:4.15.1") // Latest version
    kapt("com.github.bumptech.glide:compiler:4.15.1")

    testImplementation(libs.junit)
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.0")
    testImplementation("androidx.arch.core:core-testing:2.1.0")
    testImplementation( "junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-inline:4.0.0")
    testImplementation("org.mockito:mockito-core:4.0.0")

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}