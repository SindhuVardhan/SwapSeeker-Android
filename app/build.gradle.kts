plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.firebase-perf")
}

android {
    namespace = "com.example.swapseeker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.swapseeker"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.android.volley:volley:1.2.1")
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.6")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.google.mlkit:common:18.10.0")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
    implementation("com.google.firebase:firebase-firestore-ktx:24.10.1")
    implementation("com.google.android.gms:play-services-analytics-impl:18.0.4")
    implementation ("com.razorpay:checkout:1.6.33")
//    implementation ("com.whatsapp:whatsapp-android:2.21.3")
    implementation("com.google.firebase:firebase-perf:20.5.2")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}