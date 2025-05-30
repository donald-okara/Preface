import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    id("kotlin-kapt")
}

val keystoreProps = Properties().apply {
    val keystoreFile = rootProject.file("local.properties")
    if (keystoreFile.exists()) {
        load(FileInputStream(keystoreFile))
    }
}

android {
    namespace = "ke.don.shared_navigation"
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
    implementation(project(":common-datasource"))
    implementation(project(":shared-components"))
    kapt(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.foundation)
    implementation(libs.androidx.navigation.runtime.ktx)

    //Voyager Experimenting for now
    implementation(libs.voyager.navigator) // Core navigation
    implementation(libs.voyager.androidx) // Jetpack Compose support
    implementation(libs.voyager.tab.navigator)



    implementation(project(":common-domain"))
    implementation(project(":feature-book-details"))
    implementation(project(":feature-authentication"))
    implementation(project(":feature-bookshelf"))
    implementation(project(":feature-profile"))
    implementation(libs.androidx.navigation.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}