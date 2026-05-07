import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.kotlin.serialization)

    alias(libs.plugins.ksp)
    id("com.google.dagger.hilt.android")
}
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}
val supabaseUrl = localProperties.getProperty("SUPABASE_URL")?.trim() ?: ""
val supabaseKey = localProperties.getProperty("SUPABASE_KEY")?.trim() ?: ""

val sightengineUser = localProperties.getProperty("SIGHTENGINE_USER")?.trim() ?: ""
val sightengineSecret = localProperties.getProperty("SIGHTENGINE_SECRET")?.trim() ?: ""
val geminiKey = localProperties.getProperty("GEMINI_KEY")?.trim() ?: ""


android {
    namespace = "com.example.smartpick"
    compileSdk = 36

    // 1. Thêm cấu hình ký (Signing Config) trước buildTypes
    signingConfigs {
        getByName("debug") {
            // file("debug.keystore") trỏ trực tiếp vào file trong thư mục app/
            storeFile = file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    defaultConfig {
        applicationId = "com.example.smartpick"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // SUPABASE -----------------------
        buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
        buildConfigField("String", "SUPABASE_KEY", "\"$supabaseKey\"")
        //------------------------
        println("DEBUG: Supabase URL là: $supabaseUrl")

        // SIGHTENGINE --------------------
        buildConfigField("String", "SIGHTENGINE_USER", "\"$sightengineUser\"")
        buildConfigField("String", "SIGHTENGINE_SECRET", "\"$sightengineSecret\"")
        //------------------------

        // GEMINI_KEY ----------------------
        buildConfigField("String", "GEMINI_KEY", "\"$geminiKey\"")
        println("BUILD_INFO: Gemini Key length: ${geminiKey.length}")
    }



    buildTypes {
        // 2. Gán cấu hình ký cho bản build debug
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions {
        unitTests.all {
            it.jvmArgs("-XX:+EnableDynamicAgentLoading")
        }
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
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    /**
     * Supabase
     */
    // Supabase bom (Quản lý phiên bản)
    implementation(platform("io.github.jan-tennert.supabase:bom:2.1.3"))

    // Auth (Đăng nhập) và Postgrest (Lưu/Đọc Database)
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:gotrue-kt")
    implementation("io.github.jan-tennert.supabase:storage-kt")


    // Ktor client (Supabase dùng cái này để gọi mạng)
    val ktorVersion = "2.3.8"
    implementation("io.ktor:ktor-client-android:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-utils:$ktorVersion")

    // Kotlinx Serialization (Để biến JSON thành Object Kotlin)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation("androidx.compose.material:material-icons-extended")

    // Thư viện đăng nhập mới nhất của Google
    implementation("androidx.credentials:credentials:1.2.1")
    implementation("androidx.credentials:credentials-play-services-auth:1.2.1")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")

    // Thư viện ViewModel cho Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Thư viện hỗ trợ hiltViewModel() trong Compose
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-android-compiler:2.51.1")

    implementation("io.coil-kt:coil-compose:2.6.0")
    // Thêm thư viện hỗ trợ video cho Coil
    implementation("io.coil-kt:coil-video:2.6.0")

    // OkHttp (Sử dụng cho ModerationService)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Testing
    testImplementation(libs.junit)
    testImplementation("org.json:json:20240303")
    testImplementation("io.mockk:mockk:1.13.10")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    testImplementation("org.slf4j:slf4j-simple:2.0.7")

}