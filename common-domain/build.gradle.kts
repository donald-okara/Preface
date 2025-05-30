import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    alias(libs.plugins.kotlin.serialization)
}

// app/build.gradle (or module-level build.gradle)
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}
val keystoreProps = Properties().apply {
    val keystoreFile = rootProject.file("local.properties")
    if (keystoreFile.exists()) {
        load(FileInputStream(keystoreFile))
    }
}

android {
    namespace = "ke.don.shared_domain"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField(
            "String",
            "WEB_CLIENT_ID",
            "\"${localProperties.getProperty("WEB_CLIENT_ID")}\""
        )

        buildConfigField(
            "String",
            "GOOGLE_API_KEY",
            "\"${localProperties.getProperty("GOOGLE_API_KEY")}\""
        )

        buildConfigField(
            "String",
            "SUPABASE_URL",
            "\"${localProperties.getProperty("SUPABASE_URL")}\""
        )

        buildConfigField(
            "String",
            "SUPABASE_KEY",
            "\"${localProperties.getProperty("SUPABASE_KEY")}\""
        )
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
    buildFeatures {
        buildConfig = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.palette.ktx)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)


    implementation(libs.androidx.material3.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}