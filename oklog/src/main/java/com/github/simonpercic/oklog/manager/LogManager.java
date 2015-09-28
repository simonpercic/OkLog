package com.github.simonpercic.oklog.manager;

import android.text.TextUtils;

import com.github.simonpercic.oklog.utils.Constants;
import com.github.simonpercic.oklog.utils.StringUtils;

import java.io.IOException;

import timber.log.Timber;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public final class LogManager {

    private LogManager() {
    }

    public static void log(String body) {
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

        Timber.d(String.format("%s - %s%s", Constants.LOG_TAG, Constants.LOG_URL_BASE, compressed));
    }
}
