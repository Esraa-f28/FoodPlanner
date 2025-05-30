plugins {
    alias(libs.plugins.android.application)
        id("com.google.gms.google-services")
}

android {
    namespace = "com.example.foodplanner"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.foodplanner"
        minSdk = 24
        targetSdk = 35
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    //implementation(libs.room.common.jvm)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    implementation ("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")

    implementation ("androidx.room:room-runtime:2.6.1")
    annotationProcessor ("androidx.room:room-compiler:2.6.1")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")


    implementation ("com.google.android.material:material:1.6.0")

    implementation ("com.prolificinteractive:material-calendarview:1.4.3")
    implementation ("com.android.support:support-compat:25.1.1")
    implementation ("com.google.firebase:firebase-auth:21.0.1")
    implementation ("com.google.android.gms:play-services-auth:20.0.1")
    implementation ("com.google.firebase:firebase-database:20.0.3")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))

    implementation("com.google.firebase:firebase-analytics")
    implementation("com.airbnb.android:lottie:6.3.0")


}