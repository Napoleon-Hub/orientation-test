import com.android.build.api.artifact.SingleArtifact
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

val appVersionMajor = 3
val appVersionMinor = 2
val appVersionPatch = 1

val keystorePropertiesFile: File = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(keystorePropertiesFile.inputStream())
}

android {
    namespace = "com.funnygaytest"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.funnygaytest"
        minSdk = 24
        targetSdk = 37

        versionCode = generateVersionCode(appVersionMajor, appVersionMinor, appVersionPatch)
        versionName = generateVersionName(appVersionMajor, appVersionMinor, appVersionPatch)
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("debug.keystore")
        }
        create("release") {
            if (keystorePropertiesFile.exists()) {
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
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            if (keystorePropertiesFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    androidResources {
        @Suppress("UnstableApiUsage")
        generateLocaleConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

androidComponents {
    onVariants(selector().withBuildType("release")) { variant ->
        val versionName = generateVersionName(appVersionMajor, appVersionMinor, appVersionPatch)
        val mappingFile = variant.artifacts.get(SingleArtifact.OBFUSCATION_MAPPING_FILE)
        val taskSuffix = variant.name.replaceFirstChar { it.uppercase() }

        val archiveMapping = tasks.register<Tar>("archive${taskSuffix}Mapping") {
            group = "release"
            description = "Архивирует mapping.txt и отчёты R8 для версии $versionName"

            compression = Compression.GZIP
            archiveFileName.set("mapping-$versionName.tar.gz")
            destinationDirectory.set(rootProject.layout.projectDirectory.dir("mapping-archive"))

            from(mappingFile.map { it.asFile.parentFile }) {
                include(
                    "mapping.txt",
                    "usage.txt",
                    "seeds.txt",
                    "configuration.txt",
                    "resources.txt"
                )
            }

            doLast {
                logger.lifecycle("Маппинг заархивирован: ${archiveFile.get().asFile.path}")
            }
        }

        tasks.matching { it.name == "assembleRelease" || it.name == "bundleRelease" }
            .configureEach { finalizedBy(archiveMapping) }
    }
}

base {
    archivesName.set("funnygaytest-${generateVersionName(appVersionMajor, appVersionMinor, appVersionPatch)}")
}

dependencies {
    // Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.splashscreen)

    // Jetpack Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.material3.adaptive)
    implementation(libs.compose.runtime.livedata)
    implementation(libs.lottie.compose)

    // Activity
    implementation(libs.androidx.activity.compose)

    // Lifecycle
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.livedata.ktx)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.lifecycle.viewmodel.compose)
    ksp(libs.hilt.android.compiler)

    // Navigation 3
    implementation(libs.navigation3.runtime)
    implementation(libs.navigation3.ui)
    implementation(libs.lifecycle.viewmodel.navigation3)
    implementation(libs.kotlinx.serialization.core)

    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.play.services)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.config)
    releaseImplementation(libs.firebase.appcheck.playintegrity)
    debugImplementation(libs.firebase.appcheck.debug)

    // Google Play Services
    implementation(libs.play.app.update.ktx)
    implementation(libs.play.review)
    implementation(libs.play.review.ktx)
    implementation(libs.play.games)

    // Yandex Mobile Ads
    implementation(libs.yandex.mobile.ads)
    implementation(libs.yandex.mobile.ads.applovin)
    implementation(libs.yandex.mobile.ads.unityads)

    // Billing
    implementation(libs.billing.ktx)

    // Work Runtime
    constraints { implementation(libs.work.runtime) }

    // DataStore
    implementation(libs.datastore.preferences)
    implementation(libs.kotlinx.serialization.json)

    // Timber
    implementation(libs.timber)
}

fun generateVersionCode(major: Int, minor: Int, patch: Int): Int {
    require(minor in 0..99 && patch in 0..99) { "minor и patch должны быть в диапазоне 0..99" }
    return major * 10_000 + minor * 100 + patch
}
fun generateVersionName(major: Int, minor: Int, patch: Int): String = "$major.$minor.$patch"