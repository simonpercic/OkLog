package com.github.simonpercic.oklog.core;

import android.util.Log;

import com.github.simonpercic.oklog.shared.LogDataSerializer;
import com.github.simonpercic.oklog.shared.SharedConstants;
import com.github.simonpercic.oklog.shared.data.LogData;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private final boolean withRequestBody;
    private final boolean shortenInfoUrl;
    @NotNull private final LogDataConfig logDataConfig;

    /**
     * Constructor.
     *
     * @param urlBase url base to use
     * @param logInterceptor optional log interceptor
     * @param useAndroidLog true to use Android's Log methods, false to use Timber
     * @param withRequestBody true to include request body
     * @param shortenInfoUrl true to shorten info url on the server-side
     * @param logDataConfig log data config
     */
    public LogManager(String urlBase, LogInterceptor logInterceptor, boolean useAndroidLog, boolean withRequestBody,
            boolean shortenInfoUrl, @NotNull LogDataConfig logDataConfig) {
        this.logUrlBase = urlBase;
        this.logInterceptor = logInterceptor;
        this.useAndroidLog = useAndroidLog || !TimberUtils.hasTimber();
        this.withRequestBody = withRequestBody;
        this.shortenInfoUrl = shortenInfoUrl;
        this.logDataConfig = logDataConfig;
    }

    /**
     * Logs response data.
     *
     * @param data response data
     */
    public void log(LogDataBuilder data) {
        LogData logData = LogDataConverter.convert(data, logDataConfig);
        String logUrl = getLogUrl(data.getResponseBody(), data.getRequestBody(), logData);

        if (logInterceptor == null || !logInterceptor.onLog(logUrl)) {
            logDebug(logUrl, data.getRequestMethod(), data.getRequestUrlPath());
        }
    }

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

        StringBuilder queryParams = new StringBuilder();

        if (withRequestBody) {
            queryParams = getRequestBodyQuery(queryParams, requestBody);
        }

        queryParams = getLogDataQuery(queryParams, logData);

        boolean infoUrl = withRequestBody || shortenInfoUrl || logDataConfig.any();

        if (shortenInfoUrl) {
            queryParams = appendQuerySymbol(queryParams, SharedConstants.QUERY_SHORTEN_URL, "1");
        }

        String urlPath = infoUrl ? Constants.LOG_URL_INFO_PATH : Constants.LOG_URL_ECHO_PATH;

        String url = String.format("%s%s%s%s", logUrlBase, Constants.LOG_URL_BASE_PATH, urlPath, responseBodyString);

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

    @NotNull
    private StringBuilder getRequestBodyQuery(@NotNull StringBuilder queryParams, @Nullable String requestBody) {
        String requestBodyString = compressBody(requestBody);

        return appendQuerySymbol(queryParams, SharedConstants.QUERY_PARAM_REQUEST_BODY, requestBodyString);
    }

    @NotNull private StringBuilder getLogDataQuery(@NotNull StringBuilder queryParams, @Nullable LogData logData) {
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

        return appendQuerySymbol(queryParams, SharedConstants.QUERY_PARAM_DATA, logDataString);
    }

    void logDebug(String logUrl, String requestMethod, String requestUrlPath) {
        String format = "%s - %s %s - %s";

        if (useAndroidLog) {
            Log.d(Constants.LOG_TAG, String.format(format, Constants.LOG_TAG, requestMethod, requestUrlPath, logUrl));
        } else {
            Timber.d(format, Constants.LOG_TAG, requestMethod, requestUrlPath, logUrl);
        }
    }

    @NotNull private static StringBuilder appendQuerySymbol(@NotNull StringBuilder queryParams, String querySymbol,
            String string) {

        if (!StringUtils.isEmpty(string)) {
            boolean first = queryParams.length() == 0;
            queryParams.append(first ? "?" : "&");
            queryParams.append(querySymbol);
            queryParams.append('=');
            queryParams.append(string);
        }

        return queryParams;
    }
}
