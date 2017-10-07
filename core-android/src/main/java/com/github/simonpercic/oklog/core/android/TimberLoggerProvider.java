package com.github.simonpercic.oklog.core.android;

import com.github.simonpercic.oklog.core.Logger;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public final class TimberLoggerProvider {

    private TimberLoggerProvider() {
        // no instance
    }

    public static Logger provideLogger() {
        return new TimberLogger();
    }
}
