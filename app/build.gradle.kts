plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.hilt)
    // ADR-45: consumer / app-target side of the baseline profile plugin. The
    // same plugin id is applied in :baselineprofile (producer) - the marker POM
    // dispatches to producer/consumer/apptarget internally.
    alias(libs.plugins.androidx.baselineprofile)
}

android {
    namespace = "com.momentum.app"
    // Phase C (ADR-42 consequence): the nav3-runtime / nav3-ui /
    // lifecycle-viewmodel-navigation3 AARs declare
    // `aarMetadata.minCompileSdk = 36` (verified in each AAR), so compileSdk
    // must reach 36 or :app:check*AarMetadata fails. targetSdk stays 35
    // (unchanged behavior/compliance), minSdk stays 26.
    compileSdk = 36

    defaultConfig {
        applicationId = "com.momentum.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // Phase F: R8 full mode (AGP 8+ default) + resource shrinking.
            // Behavior is identical to debug — the release build is the same
            // code, only shrunk/obfuscated. Keep rules: app/proguard-rules.pro.
            // No signingConfig: release is unsigned by default and signing is
            // Human-in-the-Loop (AGENTS.md §12) — no keystore is created here.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // AGP 9 built-in Kotlin derives jvmTarget from compileOptions (both 17);
    // the former kotlinOptions {} block is removed — ADR-41.
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        // AGP 8+ no longer generates BuildConfig by default; Settings shows the version.
        buildConfig = true
    }
    room {
        schemaDirectory("$projectDir/schemas")
    }

    testOptions {
        unitTests.all {
            // Robolectric 4.14 defaults to LEGACY SQLite on Windows (per-thread
            // connection tracking). Room 2.7.1's connection pool uses
            // connections across threads, which breaks that shadow and surfaces
            // as UncaughtExceptionsBeforeTest via kotlinx-coroutines-test.
            // NATIVE is Robolectric's default on every non-Windows host.
            it.systemProperty("robolectric.sqliteMode", "NATIVE")
        }
    }
}

// ADR-45: consumer-side baseline profile configuration.
// No signingConfig is added and the real `release` build type is untouched: the
// plugin creates its own synthetic profileable build types and signs those with
// the module's debug signing config. Signing of shipped artifacts stays
// Human-in-the-Loop (AGENTS.md 12).
baselineProfile {
    variants.configureEach {
        automaticGenerationDuringBuild = false // never require a device at build time
        saveInSrc = true                        // write the profile back into the source tree
        mergeIntoMain = true                    // app/src/main/baseline-prof.txt, not src/release/...
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    // Resolves 1.9.3 → 1.12.0 anyway: navigation3-ui-android:1.1.0 and
    // lifecycle-viewmodel-navigation3-android:2.10.0 require
    // activity-compose 1.12.0 (§8.2 "bump only if resolution forces").
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    // Navigation 3 (ADR-42): NavKey back stack + NavDisplay. The Nav2
    // `androidx-navigation-compose` bridge is removed; hilt-navigation-compose
    // still drags an unused navigation-compose 2.5.1 transitively (§8.4 #9).
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)
    // WorkManager is declared for the Phase 9 notification architecture.
    // It is an approved exception to "no background deps" — local-only reminders.
    implementation(libs.androidx.work.runtime)

    // Hilt DI (ADR-43): core + androidx integrations; compiled with KSP (never kapt).
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)

    // Phase F (skill pin): installs/reads any baseline profile shipped in the APK.
    // Momentum has no profile yet (a baselineprofile/Macrobenchmark module is out
    // of scope), so this is the runtime half only — it is also what makes a future
    // profile consumable without another dependency change.
    implementation(libs.androidx.profileinstaller)

    // ADR-45: the generated profile producer module. `saveInSrc` + `mergeIntoMain`
    // above mean AGP packages the committed app/src/main/baseline-prof.txt into
    // assets/dexopt/baseline.prof for every variant automatically.
    baselineProfile(project(":baselineprofile"))

    ksp(libs.androidx.room.compiler)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
