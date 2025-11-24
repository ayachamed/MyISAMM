import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

// Load properties from gradle.properties
val properties = Properties()
val localPropertiesFile = rootProject.file("gradle.properties")
if (localPropertiesFile.exists()) {
    properties.load(FileInputStream(localPropertiesFile))
} else {
    println("Warning: gradle.properties file not found at project root.")
}

// Determine Google Maps API Key
val mapsApiKey = properties.getProperty("GOOGLE_MAPS_API_KEY")
val resolvedMapsKey = if (mapsApiKey == null && !gradle.startParameter.taskNames.any {
        it.contains("lint", ignoreCase = true) || it.contains("assemble", ignoreCase = true)
    }) {
    println("Warning: GOOGLE_MAPS_API_KEY not found. Maps may fail.")
    "DEFAULT_API_KEY_PLACEHOLDER_APP_WILL_PROBABLY_CRASH_OR_MAPS_WONT_WORK"
} else {
    mapsApiKey ?: "DEFAULT_API_KEY_PLACEHOLDER_APP_WILL_PROBABLY_CRASH_OR_MAPS_WONT_WORK"
}

android {
    namespace = "com.example.myisamm"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myisamm"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["googleMapsApiKey"] = resolvedMapsKey
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

    // Uncomment if needed
    // buildFeatures {
    //     viewBinding = true
    //     dataBinding = true
    // }
}

dependencies {
    implementation(platform(libs.firebase.bom))

    implementation(libs.appcompat)
    implementation(libs.material)

    // Navigation (non-KTX for Java projects)
    implementation(libs.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    implementation(libs.play.services.maps)

    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
