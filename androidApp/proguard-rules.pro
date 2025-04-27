# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
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

# Keep Compose runtime
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.foundation.** { *; }
-keep class androidx.compose.material.** { *; }

# Keep Ktor
-keep class io.ktor.** { *; }
-keep class kotlinx.serialization.** { *; }

# Keep Koin
-keep class org.koin.** { *; }

# Keep coroutines
-keep class kotlinx.coroutines.** { *; }

# Keep your application classes
-keep class ru.mamykin.exchange.** { *; }

-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.RuntimeMXBean