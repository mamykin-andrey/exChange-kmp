import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    kotlin("kapt")
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(project(":shared"))
            implementation(libs.androidx.material)

            implementation(platform("androidx.compose:compose-bom:2024.06.00"))
            implementation(libs.activity.compose)
            implementation(libs.androidx.ui)
            implementation(libs.androidx.ui.graphics)
            implementation(libs.androidx.material3)

            implementation("com.github.stephanenicolas.toothpick:ktp:3.1.0")
            implementation("com.squareup.picasso:picasso:2.71828")

//            testImplementation "junit:junit:4.13.2"
//            testImplementation "org.mockito:mockito-core:5.10.0"
//            testImplementation "org.mockito.kotlin:mockito-kotlin:5.2.1"
//            testImplementation "androidx.arch.core:core-testing:2.2.0"
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(projects.shared)
        }
    }
}

android {
    namespace = "ru.mamykin.exchange"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "ru.mamykin.exchange"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    kapt {
        arguments {
            arg("toothpick_registry_package_name", "ru.mamykin.exchange")
        }
    }
    dependencies {
        debugImplementation(compose.uiTooling)
        add("kapt", "com.github.stephanenicolas.toothpick:toothpick-compiler:3.1.0")
    }
}