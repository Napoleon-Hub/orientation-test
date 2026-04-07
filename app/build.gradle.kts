import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

val appVersionMajor = 3
val appVersionMinor = 0
val appVersionPatch = 0

val keystorePropertiesFile: File? = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile != null && keystorePropertiesFile.exists()) {
    keystoreProperties.load(keystorePropertiesFile.inputStream())
}

android {
    namespace = "com.funnygaytest"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.funnygaytest"
        minSdk = 24
        targetSdk = 36

        versionCode = generateVersionCode(appVersionMajor, appVersionMinor, appVersionPatch)
        versionName = generateVersionName(appVersionMajor, appVersionMinor, appVersionPatch)
    }

    signingConfigs {
        create("release") {
            if (keystorePropertiesFile != null && keystorePropertiesFile.exists()) {
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            if (keystorePropertiesFile != null && keystorePropertiesFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

base {
    archivesName.set("funnygaytest-${generateVersionName(appVersionMajor, appVersionMinor, appVersionPatch)}")
}

dependencies {
    // Android
    implementation(libs.androidx.core.ktx)

    // Jetpack Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.material.icons)
    implementation(libs.compose.runtime.livedata)
    implementation(libs.lottie.compose)

    // Activity
    implementation(libs.androidx.activity.compose)

    // Lifecycle
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.livedata.ktx)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)

    // Navigation Components
    implementation(libs.navigation.ui.ktx)

    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    // Google Play
    implementation(libs.billing.ktx)
    implementation(libs.play.app.update.ktx)
    implementation(libs.user.messaging.platform)

    // Yandex Mobile Ads
    //implementation(libs.yandex.mobile.ads)

    // Work Runtime
    constraints { implementation(libs.work.runtime) }
}

fun generateVersionCode(major: Int, minor: Int, patch: Int): Int = major * 100 + minor * 10 + patch
fun generateVersionName(major: Int, minor: Int, patch: Int): String = "$major.$minor.$patch"