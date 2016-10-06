package com.github.simonpercic.oklog.core;

import android.support.annotation.Nullable;
import android.util.Log;

import com.github.simonpercic.oklog.shared.LogDataSerializer;
import com.github.simonpercic.oklog.shared.data.LogData;

import java.io.IOException;

import timber.log.Timber;

/**
 * Log manager.
 * Logs the received response body.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public class LogManager {

    private static final String LOG_FORMAT = "LogManager: %s";

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
        LogData logData = LogDataConverter.convert(data);
        String logUrl = getLogUrl(data.getResponseBody(), logData);

        if (logInterceptor == null || !logInterceptor.onLog(logUrl)) {
            logDebug(logUrl);
        }
    }

    String getLogUrl(@Nullable String responseBody, @Nullable LogData logData) {
        String responseBodyString;

        try {
            responseBodyString = CompressionUtils.gzipBase64UrlSafe(responseBody);
        } catch (IOException e) {
            if (useAndroidLog) {
                Log.e(Constants.LOG_TAG, String.format(LOG_FORMAT, e.getMessage()));
            } else {
                Timber.e(e, LOG_FORMAT, e.getMessage());
            }

            return null;
        }

        if (responseBodyString == null || responseBodyString.length() == 0) {
            String message = "LogManager: responseBodyString string is empty";
            if (useAndroidLog) {
                Log.w(Constants.LOG_TAG, message);
            } else {
                Timber.w(message);
            }

            responseBodyString = "0";
        }

        String url = String.format("%s%s%s", logUrlBase, Constants.LOG_URL_ECHO_RESPONSE_PATH, responseBodyString);

        byte[] logDataBytes = LogDataSerializer.serialize(logData);

        String logDataString = null;
        try {
            logDataString = CompressionUtils.gzipBase64UrlSafe(logDataBytes);
        } catch (IOException e) {
            if (useAndroidLog) {
                Log.e(Constants.LOG_TAG, String.format(LOG_FORMAT, e.getMessage()));
            } else {
                Timber.e(e, LOG_FORMAT, e.getMessage());
            }
        }

        if (!StringUtils.isEmpty(logDataString)) {
            url = String.format("%s?d=%s", url, logDataString);
        }

        return url;
    }

    void logDebug(String logUrl) {
        if (useAndroidLog) {
            Log.d(Constants.LOG_TAG, String.format("%s - %s", Constants.LOG_TAG, logUrl));
        } else {
            Timber.d("%s - %s", Constants.LOG_TAG, logUrl);
        }
    }
}
