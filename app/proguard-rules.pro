# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


# Krossbow WebSocket - JVM-only classes not available on Android
-dontwarn java.net.http.HttpResponse
-dontwarn java.net.http.WebSocketHandshakeException
-dontwarn org.hildan.krossbow.websocket.ktor.**

# Ktor & Kotlinx.Serialization
-keep class io.ktor.** { *; }
-keep class * extends io.ktor.** { *; }
-dontwarn io.ktor.**

-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keep,allowobfuscation,allowoptimization class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}

# Keep all DTOs to prevent issues with JSON serialization
-keep class com.tourly.app.**.dto.** { *; }
-keep class com.tourly.app.core.network.BackendErrorResponse { *; }

# Keep domain models and enums that might be serialized (like UserRole)
-keep class com.tourly.app.**.domain.model.** { *; }
-keepclassmembers enum com.tourly.app.**.domain.** { *; }
-keepclassmembers enum com.tourly.app.**.data.** { *; }
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Google Auth and Credential manager classes to prevent reflection/parsing issues
-keep class androidx.credentials.** { *; }
-keep class com.google.android.libraries.identity.googleid.** { *; }

# Google Places SDK
-keep class com.google.android.libraries.places.** { *; }
-dontwarn com.google.android.libraries.places.**