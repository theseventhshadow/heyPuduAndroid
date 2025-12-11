plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.kotlin.serialization)
    id("jacoco")
}

android {
    namespace = "com.heypudu.heypudu"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.heypudu.heypudu"
        minSdk = 26
        targetSdk = 36
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigationevent)
    implementation(libs.material)
    implementation("androidx.compose.material:material-icons-extended:1.6.0")
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation(libs.androidx.compose.foundation) {
        exclude(group = "com.intellij", module = "annotations")
        exclude(group = "org.jetbrains", module = "annotations")
    }
    implementation("androidx.compose.material:material")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.compose.material3:material3-window-size-class:1.4.0")
    implementation("androidx.room:room-runtime:2.6.1") {
        exclude(group = "com.intellij", module = "annotations")
        exclude(group = "org.jetbrains", module = "annotations")
    }
    implementation("androidx.room:room-compiler:2.6.1") {
        exclude(group = "com.intellij", module = "annotations")
        exclude(group = "org.jetbrains", module = "annotations")
    }
    implementation("androidx.room:room-ktx:2.6.1") {
        exclude(group = "com.intellij", module = "annotations")
        exclude(group = "org.jetbrains", module = "annotations")
    }

    // Retrofit & OkHttp
    implementation(libs.retrofit)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation(libs.okhttp)
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Unit Test Dependencies - Only JUnit and Truth
    testImplementation(libs.truth)
    testImplementation(libs.junit)

    // Android Test Dependencies
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}

// JaCoCo configuration for code coverage reporting
jacoco {
    toolVersion = "0.8.10"
}

tasks.register("jacocoTestReport") {
    dependsOn("testDebugUnitTest")
    description = "Generate JaCoCo coverage report"
    group = "verification"
}