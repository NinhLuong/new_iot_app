plugins {
    id("com.android.application")
    id("com.google.gms.google-services")

}


android {
    namespace = "com.example.iot_app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.iot_app"
        minSdk = 27
        targetSdk = 33
        versionCode = 2
        versionName = "2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures{
        viewBinding = true
        dataBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.22")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("androidx.fragment:fragment-ktx:1.6.2")
    implementation ("androidx.activity:activity-ktx:1.8.1")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation ("androidx.cardview:cardview:1.0.0")
    implementation("com.loopj.android:android-async-http:1.4.11")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation ("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation ("androidx.legacy:legacy-support-v4:1.0.0")
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.6.0"))

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")

    // Also add the dependency for the Google Play services library and specify its version
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.github.bumptech.glide:glide:4.11.0")
    implementation("com.google.firebase:firebase-config:21.6.0")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("androidx.activity:activity:1.8.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.11.0")
    implementation ("io.github.chaosleung:pinview:1.4.4")
    implementation ("com.github.mukeshsolanki.android-otpview-pinview:otpview:3.1.0")
    implementation ("com.github.mukeshsolanki.android-otpview-pinview:otpview-compose:3.1.0")


    implementation("androidx.security:security-crypto:1.0.0")
    implementation ("com.airbnb.android:lottie:6.2.0")

    implementation ("com.google.android.gms:play-services-auth:20.7.0")
    implementation ("com.google.android.gms:play-services-auth-api-phone:18.0.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}