package com.github.simonpercic.oklog.manager;

import com.github.simonpercic.oklog.LogInterceptor;
import com.github.simonpercic.oklog.utils.Constants;
import com.github.simonpercic.oklog.utils.StringUtils;

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

    /**
     * Constructor.
     *
     * @param urlBase url base to use
     * @param logInterceptor optional log interceptor
     */
    public LogManager(String urlBase, LogInterceptor logInterceptor) {
        this.logUrlBase = urlBase;
        this.logInterceptor = logInterceptor;
    }

    /**
     * Logs response body using Timber.
     *
     * @param body response body
     */
    public void log(String body) {
        String logUrl = getLogUrl(body);

        if (logInterceptor == null || !logInterceptor.onLog(logUrl)) {
            logDebug(logUrl);
        }
    }

    String getLogUrl(String body) {
        String compressed;

        try {
            compressed = StringUtils.gzipBase64(body);
        } catch (IOException e) {
            Timber.e(e, "LogManager: %s", e.getMessage());
            return null;
        }

        if (compressed == null || compressed.length() == 0) {
            Timber.w("LogManager: compressed string is empty");
            return null;
        }

        compressed = compressed.replaceAll("\n", "");

        return String.format("%s%s%s", logUrlBase, Constants.LOG_URL_ECHO_RESPONSE_PATH, compressed);
    }

    void logDebug(String logUrl) {
        Timber.d("%s - %s", Constants.LOG_TAG, logUrl);
    }
}
