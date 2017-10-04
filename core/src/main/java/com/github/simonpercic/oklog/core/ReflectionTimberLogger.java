package com.github.simonpercic.oklog.core;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Timber logger implementation, using reflection.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@SuppressWarnings("TryWithIdenticalCatches") class ReflectionTimberLogger implements Logger {

    @Nullable private final Method debug;
    @Nullable private final Method warn;
    @Nullable private final Method error;
    private final Object[] emptyObjects = {};

    ReflectionTimberLogger() {
        this.debug = ReflectionUtils.getMethod(Constants.TIMBER_CLASS, "d", String.class, Object[].class);
        this.warn = ReflectionUtils.getMethod(Constants.TIMBER_CLASS, "w", String.class, Object[].class);
        this.error = ReflectionUtils.getMethod(Constants.TIMBER_CLASS, "e", Throwable.class, String.class,
                Object[].class);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void d(String tag, String message) {
        if (debug != null) {
            try {
                debug.invoke(null, message, emptyObjects);
            } catch (IllegalAccessException e) {
                // ignored
            } catch (InvocationTargetException e) {
                // ignored
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override public void w(String tag, String message) {
        if (warn != null) {
            try {
                warn.invoke(null, message, emptyObjects);
            } catch (IllegalAccessException e) {
                // ignored
            } catch (InvocationTargetException e) {
                // ignored
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override public void e(String tag, String message, Throwable throwable) {
        if (error != null) {
            try {
                error.invoke(null, throwable, message, emptyObjects);
            } catch (IllegalAccessException e) {
                // ignored
            } catch (InvocationTargetException e) {
                // ignored
            }
        }
    }

    boolean isValid() {
        return debug != null && warn != null && error != null;
    }
}
