plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.projectapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.projectapplication"
        minSdk = 24
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    viewBinding {
        enable = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    //xml
    implementation("com.tickaroo.tikxml:annotation:0.8.13")
    implementation("com.tickaroo.tikxml:core:0.8.13")
    implementation("com.tickaroo.tikxml:retrofit-converter:0.8.13")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    kapt ("com.tickaroo.tikxml:processor:0.8.13")


    implementation("com.github.bumptech.glide:glide:4.16.0") //이미지 다루는데 도움이 되는 라이브러리

    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))

    implementation("com.google.firebase:firebase-auth-ktx:23.0.0")
    implementation("androidx.multidex:multidex:2.0.1")

    implementation("com.google.android.gms:play-services-auth:21.1.1")
    implementation("com.google.firebase:firebase-firestore-ktx:25.0.0")

    implementation("com.google.firebase:firebase-storage-ktx:21.0.0")
    implementation("androidx.preference:preference:1.2.1")

    //splash
    implementation("androidx.core:core-splashscreen:1.0.1")

    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")
}