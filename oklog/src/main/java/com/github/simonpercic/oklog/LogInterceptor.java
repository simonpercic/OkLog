package com.github.simonpercic.oklog;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public interface LogInterceptor {
    boolean onLog(String url);
}
