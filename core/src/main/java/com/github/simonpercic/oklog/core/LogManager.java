package com.github.simonpercic.oklog.core;

import android.util.Log;

import java.io.IOException;

import timber.log.Timber;

/**
 * Log manager.
 * Logs the received response body.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public class LogManager {

    private final String logUrlBase;
    private final LogInterceptor logInterceptor;
    final boolean useAndroidLog;

    /**
     * Constructor.
     *
     * @param urlBase url base to use
     * @param logInterceptor optional log interceptor
     * @param useAndroidLog true to use Android's Log methods, false to use Timber
     */
    public LogManager(String urlBase, LogInterceptor logInterceptor, boolean useAndroidLog) {
        this.logUrlBase = urlBase;
        this.logInterceptor = logInterceptor;
        this.useAndroidLog = useAndroidLog || !TimberUtils.hasTimber();
    }

    /**
     * Logs response data.
     *
     * @param data response data
     */
    public void log(LogDataBuilder data) {
        String logUrl = getLogUrl(data);

        if (logInterceptor == null || !logInterceptor.onLog(logUrl)) {
            logDebug(logUrl);
        }
    }

    String getLogUrl(LogDataBuilder data) {
        String compressed;

        try {
            compressed = StringUtils.gzipBase64(data.getResponseBody());
        } catch (IOException e) {
            if (useAndroidLog) {
                Log.e(Constants.LOG_TAG, String.format("LogManager: %s", e.getMessage()));
            } else {
                Timber.e(e, "LogManager: %s", e.getMessage());
            }

            return null;
        }

        if (compressed == null || compressed.length() == 0) {
            String message = "LogManager: compressed string is empty";
            if (useAndroidLog) {
                Log.w(Constants.LOG_TAG, message);
            } else {
                Timber.w(message);
            }

            return null;
        }

        compressed = compressed.replaceAll("\n", "");

        return String.format("%s%s%s", logUrlBase, Constants.LOG_URL_ECHO_RESPONSE_PATH, compressed);
    }

    void logDebug(String logUrl) {
        if (useAndroidLog) {
            Log.d(Constants.LOG_TAG, String.format("%s - %s", Constants.LOG_TAG, logUrl));
        } else {
            Timber.d("%s - %s", Constants.LOG_TAG, logUrl);
        }
    }
}
