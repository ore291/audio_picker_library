import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.PublishingExtension

plugins {
    alias(libs.plugins.android.library)
    id("maven-publish")
}

android {
    namespace = "com.knighttechapps.audio_picker"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    buildFeatures {
        viewBinding = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation(libs.core)
    implementation(libs.recyclerview)
    implementation(libs.constraintlayout)

    // For permissions
    implementation(libs.activity)
    implementation(libs.fragment)

    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.media)
}

afterEvaluate {
    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = "com.github.ore291"
                artifactId = "audio-picker"
                version = "1.0.0"
            }
        }
    }
}