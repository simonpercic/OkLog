-keep class timber.log.Timber {
    public static void d(...);
    public static void w(...);
    public static void e(...);
}

-keep class com.github.simonpercic.oklog.core.android.TimberLoggerProvider {
    *;
}
