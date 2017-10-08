package com.github.simonpercic.oklog.core.android;

import com.github.simonpercic.oklog.core.Logger;
import com.github.simonpercic.oklog.core.TimberUtils;

import org.jetbrains.annotations.Nullable;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public final class TimberLoggerProvider {

    private TimberLoggerProvider() {
        // no instance
    }

    @Nullable public static Logger provideLogger() {
        if (TimberUtils.hasTimber()) {
            return new TimberLogger();
        } else {
            return null;
        }
    }
}
