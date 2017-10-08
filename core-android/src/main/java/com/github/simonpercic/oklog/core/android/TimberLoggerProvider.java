package com.github.simonpercic.oklog.core.android;

import com.github.simonpercic.oklog.core.Logger;
import com.github.simonpercic.oklog.core.ReflectionUtils;

import org.jetbrains.annotations.Nullable;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public final class TimberLoggerProvider {

    private TimberLoggerProvider() {
        // no instance
    }

    @Nullable public static Logger provideLogger() {
        if (ReflectionUtils.hasClass("timber.log.Timber")) {
            return new TimberLogger();
        } else {
            return null;
        }
    }
}
