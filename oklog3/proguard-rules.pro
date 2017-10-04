-keep class okhttp3.internal.http.HttpHeaders {
    boolean hasBody(okhttp3.Response);
}
-dontwarn okhttp3.internal.http.HttpHeaders

-keep class okhttp3.internal.http.HttpEngine {
    boolean hasBody(okhttp3.Response);
}

-keep class timber.log.Timber {
    public static void d(...);
    public static void w(...);
    public static void e(...);
}
