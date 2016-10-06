package com.github.simonpercic.oklog.core;

import java.nio.charset.Charset;

/**
 * Constants.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public final class Constants {

    public static final String LOG_URL_BASE_REMOTE = "http://responseecho-simonpercic.rhcloud.com";
    static final String LOG_TAG = "OKLOG";
    static final String LOG_URL_ECHO_RESPONSE_PATH = "/v1/re/";

    private static final String UTF8 = "UTF-8";
    static final Charset CHARSET_UTF8 = Charset.forName(UTF8);

    private Constants() {
        // no instance
    }
}
