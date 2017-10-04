##--------------- Begin: Gson ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
##--------------- End: Gson ----------

##--------------- Begin: Retrolambda ----------
-dontwarn java.lang.invoke.*
##--------------- End: Retrolambda ----------

##--------------- Begin: OkHttp ----------
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
##--------------- End: OkHttp ----------

##--------------- Begin: Retrofit ----------
-dontwarn retrofit.**
-keep class retrofit.** { *; }

-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}
##--------------- End: Retrofit ----------

##--------------- Begin: Retrofit 2 -----------
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
##--------------- End: Retrofit 2 -----------

##--------------- Begin: RxJava ----------
-keep public class rx.Single { *; }

-dontwarn sun.misc.Unsafe

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
   rx.internal.util.atomic.LinkedQueueNode producerNode;
   rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
   rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
##--------------- End: RxJava ----------

##--------------- Begin: Okio ----------
-dontwarn okio.**
##--------------- End: Okio ----------

##--------------- Begin: Model ----------
-keepclassmembers class com.github.simonpercic.oklogexample.data.api.model.** {
  <fields>;
}
##--------------- End: Model ----------

##--------------- Begin: OkHttp 3.8.0 ----------
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault
##--------------- End: OkHttp 3.8.0 ----------
