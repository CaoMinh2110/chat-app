# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /usr/local/Cellar/android-sdk/24.3.3/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# react-native-reanimated
-keep class com.swmansion.reanimated.** { *; }
-keep class com.facebook.react.turbomodule.** { *; }

# Add any project specific keep options here:

# @generated begin expo-build-properties - expo prebuild (DO NOT MODIFY)
-keep class com.margelo.nitro.** { *; }
 -keep class com.google.android.gms.internal.consent_sdk.** { *; }
# Keep Google Ads classes
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.android.gms.internal.ads.** { *; }
# Keep Android Media classes
-keep class android.media.** { *; }
-dontwarn android.media.**        
# Keep Google Services
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.** 
# @generated end expo-build-properties