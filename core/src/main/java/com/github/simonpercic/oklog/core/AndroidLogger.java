package com.github.simonpercic.oklog.core;

import android.util.Log;

/**
 * Android logger implementation.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
class AndroidLogger implements Logger {

    /**
     * {@inheritDoc}
     */
    @Override public void d(String tag, String message) {
        Log.d(tag, message);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void w(String tag, String message) {
        Log.w(tag, message);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void e(String tag, String message, Throwable throwable) {
        Log.e(tag, message, throwable);
    }
}
