package com.github.simonpercic.oklog.core;

/**
 * String utilities.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public final class StringUtils {

    private StringUtils() {
        // no instance
    }

    /**
     * Returns <tt>true</tt> if the string is null or of zero length.
     *
     * @param string string
     * @return <tt>true</tt> if string is null or of zero length
     */
    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }
}
