package com.github.simonpercic.oklog.core;

/**
 * Log interceptor.
 * Use it to intercept the url string before it is logged.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public interface LogInterceptor {

    /**
     * Called before the url string is logged.
     * If you return true from this method, the string will not be logged.
     *
     * @param url the generated url to be logged
     * @return true if you have handled the log, false otherwise
     */
    boolean onLog(String url);
}
