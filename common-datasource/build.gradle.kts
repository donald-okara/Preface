import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

val keystoreProps = Properties().apply {
    val keystoreFile = rootProject.file("local.properties")
    if (keystoreFile.exists()) {
        load(FileInputStream(keystoreFile))
    }
}

android {
    namespace = "ke.don.common_datasource"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    signingConfigs {
        create("debugConfig") {
            storeFile = file(keystoreProps["KEYSTORE_FILE"] ?: "")
            storePassword = keystoreProps["KEYSTORE_PASSWORD"]?.toString()
            keyAlias = keystoreProps["KEY_ALIAS"]?.toString()
            keyPassword = keystoreProps["KEY_PASSWORD"]?.toString()
        }
        create("release") {
            storeFile = file(keystoreProps["KEYSTORE_FILE"] ?: "")
            storePassword = keystoreProps["KEYSTORE_PASSWORD"]?.toString()
            keyAlias = keystoreProps["KEY_ALIAS"]?.toString()
            keyPassword = keystoreProps["KEY_PASSWORD"]?.toString()
        }
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            signingConfig = signingConfigs.getByName("debugConfig")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.kotlinx.coroutines.core)

    //Dagger Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.runtime.livedata)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    //Datastore
    implementation(libs.androidx.datastore)


    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest.kt)
    implementation(libs.supabase.auth.kt)
    implementation(libs.supabase.realtime.kt)
    implementation(libs.supabase.functions.kt)

    // Retrofit with Kotlin serialization Converter
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)
    implementation (libs.converter.gson)

    implementation(libs.androidx.palette.ktx)


    //Ktor
    implementation(libs.ktor.client.android)

    //Datastore
    implementation(libs.androidx.datastore)
    implementation(libs.kotlinx.collections.immutable)

    //Room db dependencies
    implementation(libs.androidx.room.runtime)
    //noinspection KaptUsageInsteadOfKsp
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.hilt.work)

    //Project
    implementation(project(":common-domain"))


    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}