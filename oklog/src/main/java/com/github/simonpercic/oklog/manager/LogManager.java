package com.github.simonpercic.oklog.manager;

import android.text.TextUtils;

import com.github.simonpercic.oklog.LogInterceptor;
import com.github.simonpercic.oklog.utils.Constants;
import com.github.simonpercic.oklog.utils.StringUtils;

import java.io.IOException;

import timber.log.Timber;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public class LogManager {

    private final String logUrlBase;
    private final LogInterceptor logInterceptor;

    public LogManager(String url, LogInterceptor logInterceptor) {
        this.logUrlBase = url;
        this.logInterceptor = logInterceptor;
    }

    public void log(String body) {
        String compressed;

        try {
            compressed = StringUtils.gzipBase64(body);
        } catch (IOException e) {
            Timber.e(e, "LogManager: %s", e.getMessage());
            return;
        }

        if (TextUtils.isEmpty(compressed)) {
            Timber.w("LogManager: compressed string is empty");
            return;
        }

        compressed = compressed.replaceAll("\n", "");

        String logUrl = String.format("%s%s%s", logUrlBase, Constants.LOG_URL_ECHO_RESPONSE_PATH, compressed);

        if (logInterceptor == null || !logInterceptor.onLog(logUrl)) {
            Timber.d("%s - %s", Constants.LOG_TAG, logUrl);
        }
    }
}
