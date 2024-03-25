plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.movieapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.movieapp"
        minSdk = 30
        targetSdk = 34
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
    buildFeatures{
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation ("com.google.android.flexbox:flexbox:3.0.0")
    implementation("androidx.appcompat:appcompat:1.6.1")

    implementation("com.google.android.material:material:1.11.0")
    implementation ("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.1.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.1.0")

    //shimmer
    implementation ("com.facebook.shimmer:shimmer:0.1.0")

    // media player
    implementation ("androidx.media3:media3-exoplayer:1.3.0")
    implementation ("androidx.media3:media3-ui:1.3.0")
    implementation ("androidx.media3:media3-common:1.3.0")

    //login with google
    implementation("com.google.android.gms:play-services-auth:20.0.0")

    implementation ("androidx.credentials:credentials:1.2.1")
    implementation ("androidx.credentials:credentials-play-services-auth:1.2.1")
    implementation ("com.google.android.libraries.identity.googleid:googleid:1.1.0")


}