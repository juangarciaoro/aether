# ExoPlayer / Media3
-keep class androidx.media3.** { *; }

# Retrofit
-keepattributes *Annotation*
-keepattributes Signature
-keep class retrofit2.** { *; }

# Kotlinx Serialization
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class ** {
    @kotlinx.serialization.Serializable *;
}

# Coil
-keep class coil3.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Hilt
-keep class dagger.hilt.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class *

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
