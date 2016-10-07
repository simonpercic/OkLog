package com.github.simonpercic.oklog.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.github.simonpercic.oklog.shared.LogDataSerializer;
import com.github.simonpercic.oklog.shared.SharedConstants;
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
        String logUrl = getLogUrl(data.getResponseBody(), data.getRequestBody(), logData);

        if (logInterceptor == null || !logInterceptor.onLog(logUrl)) {
            logDebug(logUrl);
        }
    }

    @VisibleForTesting
    String getLogUrl(@Nullable String responseBody, @Nullable String requestBody, @Nullable LogData logData) {
        String responseBodyString = compressBody(responseBody);

        if (StringUtils.isEmpty(responseBodyString)) {
            String message = "LogManager: responseBodyString string is empty";
            if (useAndroidLog) {
                Log.w(Constants.LOG_TAG, message);
            } else {
                Timber.w(message);
            }

            responseBodyString = SharedConstants.EMPTY_RESPONSE_BODY;
        }

        String url = String.format("%s%s%s", logUrlBase, Constants.LOG_URL_ECHO_RESPONSE_PATH, responseBodyString);

        StringBuilder queryParams = new StringBuilder();

        queryParams = getRequestBodyQuery(queryParams, requestBody);
        queryParams = getLogDataQuery(queryParams, logData);

        return url.concat(queryParams.toString());
    }

    @Nullable private String compressBody(@Nullable String body) {
        String bodyString;

        try {
            bodyString = CompressionUtils.gzipBase64UrlSafe(body);
        } catch (IOException e) {
            if (useAndroidLog) {
                Log.e(Constants.LOG_TAG, String.format(LOG_FORMAT, e.getMessage()));
            } else {
                Timber.e(e, LOG_FORMAT, e.getMessage());
            }

            return null;
        }

        return bodyString;
    }

    @NonNull
    private StringBuilder getRequestBodyQuery(@NonNull StringBuilder queryParams, @Nullable String requestBody) {
        String requestBodyString = compressBody(requestBody);

        if (!StringUtils.isEmpty(requestBodyString)) {
            appendQuerySymbol(queryParams);
            queryParams.append("qb=");
            queryParams.append(requestBodyString);
        }

        return queryParams;
    }

    @NonNull private StringBuilder getLogDataQuery(@NonNull StringBuilder queryParams, @Nullable LogData logData) {
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
            appendQuerySymbol(queryParams);
            queryParams.append("d=");
            queryParams.append(logDataString);
        }

        return queryParams;
    }

    @VisibleForTesting void logDebug(String logUrl) {
        if (useAndroidLog) {
            Log.d(Constants.LOG_TAG, String.format("%s - %s", Constants.LOG_TAG, logUrl));
        } else {
            Timber.d("%s - %s", Constants.LOG_TAG, logUrl);
        }
    }

    private static void appendQuerySymbol(@NonNull StringBuilder queryParams) {
        boolean first = queryParams.length() == 0;
        queryParams.append(first ? "?" : "&");
    }
}
