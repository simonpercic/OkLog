package com.github.simonpercic.oklog.core.android;

import com.github.simonpercic.oklog.core.Logger;

import timber.log.Timber;

/**
 * Native Timber logger implementation.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
class TimberLogger implements Logger {

    /**
     * {@inheritDoc}
     */
    @Override public void d(String tag, String message) {
        Timber.d(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void w(String tag, String message) {
        Timber.w(message);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("TimberExceptionLogging")
    @Override public void e(String tag, String message, Throwable throwable) {
        Timber.e(throwable, message);
    }
}
