package com.github.simonpercic.oklog.core;

/**
 * Timber-related utils.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public final class TimberUtils {

    private TimberUtils() {
        // no instance
    }

    /**
     * Determines if Timber is available at runtime.
     *
     * @return true if Timber is available, false otherwise
     */
    public static boolean hasTimber() {
        return ReflectionUtils.hasClass(Constants.TIMBER_CLASS);
    }
}
