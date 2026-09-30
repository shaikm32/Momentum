// ADR-45 / TASK-012: the Macrobenchmark *producer* module.
//
// It has no production code. A `com.android.test` module's instrumentation
// tests ARE its main source set, which is why the generator lives in
// `src/main/kotlin` and not `src/androidTest`.
//
// AGP 9 built-in Kotlin covers this module, so `org.jetbrains.kotlin.android`
// is deliberately NOT applied here (ADR-41). No `kotlin { }` / `kotlinOptions {}`
// either - compileOptions below drives the jvmTarget.
plugins {
    id("com.android.test")
    alias(libs.plugins.androidx.baselineprofile)
}

android {
    namespace = "com.momentum.baselineprofile"
    compileSdk = 36
    defaultConfig {
        minSdk = 26
        targetSdk = 35
        // A `com.android.test` module derives its test R package from `namespace`
        // above; `testNamespace` is not part of TestDefaultConfig in AGP 9.0.0.
        // Supplies ProfileInstallReceiver + the macrobenchmark instrumentation
        // entry point. Required by BaselineProfileRule.collect.
        testInstrumentationRunner = "androidx.benchmark.junit4.AndroidBenchmarkRunner"
    }
    targetProjectPath = ":app"
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// Producer-side DSL in 1.5.0 exposes ONLY these two properties. `targetVariant`,
// `automaticGenerationDuringBuild` and `saveInSrc` are consumer-side (:app)
// properties and are configured there, not here. No managedDevices: the
// Pixel_7 AVD is booted by hand (task.md 8.7.6).
baselineProfile {
    useConnectedDevices = true
}

dependencies {
    implementation(libs.junit)
    implementation(libs.androidx.test.ext.junit)
    implementation(libs.androidx.test.uiautomator)
    implementation(libs.androidx.benchmark.junit4)
    implementation(libs.androidx.benchmark.macro.junit4)
}
