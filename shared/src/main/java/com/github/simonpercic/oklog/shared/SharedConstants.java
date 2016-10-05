package com.github.simonpercic.oklog.shared;

import java.nio.charset.Charset;

/**
 * Shared constants.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public final class SharedConstants {

    public static final String UTF8 = "UTF-8";
    public static final Charset CHARSET_UTF8 = Charset.forName(UTF8);

    private SharedConstants() {
        // no instance
    }
}
