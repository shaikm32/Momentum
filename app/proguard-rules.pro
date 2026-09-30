# Momentum R8 / ProGuard rules (Phase F).
#
# This file is deliberately minimal. It holds only what R8 cannot see by itself
# and what is NOT already covered by a consumer rule shipped inside a library
# AAR/JAR. Verified against the artifacts actually on the classpath:
#
#   androidx.room:room-runtime:2.7.1      -> proguard.txt (consumer):
#       -keep class * extends androidx.room.RoomDatabase { void <init>(); }
#       This is what keeps the KSP-generated `MomentumDatabase_Impl`: Room
#       resolves it by name (androidx.room.util.KClassUtil#
#       findAndInstantiateDatabaseImpl does `Class.forName(canonical + "_Impl")`
#       + newInstance), and no app code references it directly, so without this
#       rule R8 would shrink it away. Room 2.7 needs NO rule for @Entity/@Dao —
#       the generated code references them directly (no reflection).
#   com.google.dagger:hilt-android:2.60.1 -> proguard.txt (consumer): the four
#       `-keep,allowobfuscation,allowshrinking @dagger.hilt.*` rules covering the
#       reflective EntryPoint cast. The rest of Hilt is wired by bytecode
#       references from the Gradle plugin's ASM transform, and manifest-declared
#       components are kept by AGP's generated aapt rules. No Hilt rule needed.
#   androidx.work:work-runtime:2.9.0      -> keeps every `ListenableWorker`
#       subclass and its constructors, which is exactly what our @HiltWorker
#       classes need. No rule needed.
#   androidx.navigation3:runtime/ui:1.1.0 -> "intentionally empty ... safe to
#       shrink". No rule needed.
#   org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.1 ->
#       META-INF/com.android.tools/r8/kotlinx-serialization-r8.pro + the common
#       rules: keep `Companion`, `INSTANCE` and `serializer()` of @Serializable
#       classes. Not duplicated below.
#

# --- kotlinx.serialization + Navigation 3 back-stack restore ---------------
# androidx.navigation3.runtime.serialization.NavKeySerializer serializes a key
# as `key.javaClass.name` and deserializes it as
# `Class.forName(className).kotlin.serializer()`. That FQCN
# ("com.momentum.app.presentation.navigation.Routes$Detail") is a *runtime
# contract*: the OS persists the back stack across process death and across app
# updates, so the name must survive R8 in every build.
#
# kotlinx.serialization's own shipped rule keeps @Serializable classes with
# `allowobfuscation`, which is right for ordinary DTOs but wrong here. Pin the
# NavKey route types (and their `INSTANCE` / `serializer()` members, reachable
# only reflectively) to their names.
-keep class com.momentum.app.presentation.navigation.Routes {
    *;
}
-keep class com.momentum.app.presentation.navigation.Routes$* {
    *;
}
