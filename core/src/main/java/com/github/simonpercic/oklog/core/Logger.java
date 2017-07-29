package com.github.simonpercic.oklog.core;

/**
 * Logger.
 * Implement to do your own logging.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public interface Logger {

    /**
     * Log debug message.
     *
     * @param tag tag
     * @param message message
     */
    void d(String tag, String message);

    /**
     * Log warning message.
     *
     * @param tag tag
     * @param message message
     */
    void w(String tag, String message);

    /**
     * Log error message.
     *
     * @param tag tag
     * @param message message
     * @param throwable throwable
     */
    void e(String tag, String message, Throwable throwable);
}
