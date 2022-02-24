-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# okio

-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-keep class okio.** { *; }
-dontwarn okio.**

# Rule to avoid build errors related to SVGs.
-keep public class com.horcrux.svg.** {*;}

##########=======androidannotations=========##############
-dontwarn org.androidannotations.**
-keep class org.androidannotations.** {*;}
-keepattributes *Annotation*


-ignorewarnings


#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.onImageDownloaded.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

#for retrofit
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn retrofit2.**
-dontwarn org.codehaus.mojo.**
-keep class retrofit2.** { *; }
-dontnote retrofit2.Platform
-dontnote retrofit2.Platform$IOS$MainThreadExecutor # Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8 # Platform used when running on Java 8 VMs. Will not be used at runtime.
-keepattributes Signature # Retain generic type information for use by reflection by converters and adapters.
-keepattributes Exceptions # Retain declared checked exceptions for use by a Proxy instance.
-keepclasseswithmembers class * {
    @retrofit2.* <methods>;
}
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
# keep Retrofit 2 + Rx
-dontwarn com.octo.android.robospice.retrofit.RetrofitJackson**
-dontwarn retrofit.appengine.UrlFetchClient
-keep class com.google.gson.** { *; }
-keep class com.google.inject.** { *; }
-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }
-keep class javax.inject.** { *; }
-keep class retrofit.** { *; }
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient
-dontwarn retrofit.**
-keep interface retrofit.** { *;}
-keep class retrofit.** { *; }
-keep class retrofit.http.** { *; }
-keep class retrofit.client.** { *; }
-keep class com.squareup.** { *; }
-keep interface com.squareup.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp3.** { *; }
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *; }


#for okhttp:3.9.0
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

-keepattributes *Annotation*

-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

-keepattributes EnclosingMethod

#RXJAVA
# RxJava 0.21
-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}
-dontwarn sun.misc.Unsafe

# Google Play Services library
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *

-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }
-keep public class android.os.UserManager.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}

# Agar kompatibel support design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

# Agar kompatibel support v4 dan v7
-dontwarn android.support.**
-keep class android.support.v4.** { *; }
-keep class android.support.v7.** { *; }

# Agar support CoordinatorLayout
-keep public class * extends android.support.design.widget.CoordinatorLayout$Behavior {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>();
}

-keep class **$Properties

-dontwarn android.support.v4.**

-dontwarn com.google.**

-dontwarn sun.misc.**

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
   long producerNode;
   long consumerNode;
}

-keep class sun.misc.Unsafe { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

-keep class com.google.android.material.** { *; }

-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**

-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }


-keep class gapara.co.id.core.model.** { *; }
-keep class gapara.co.id.core.api.response.** { *; }