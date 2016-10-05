package com.github.simonpercic.oklog.core;

import android.util.Base64;

import com.github.simonpercic.oklog.shared.SharedConstants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

/**
 * Compression utilities.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public final class CompressionUtils {

    private CompressionUtils() {
        // no instance
    }

    /**
     * Compresses the given string with gzip, returns it Base64 encoded.
     *
     * @param string input string
     * @return gzipped and Base64 encoded input string
     * @throws IOException IO Exception
     */
    static String gzipBase64(String string) throws IOException {
        if (StringUtils.isEmpty(string)) {
            return string;
        }

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream(string.length());

        GZIPOutputStream gzip = new GZIPOutputStream(byteOut);
        gzip.write(string.getBytes(SharedConstants.UTF8));
        gzip.close();

        byte[] bytes = byteOut.toByteArray();

        byteOut.close();

        return Base64.encodeToString(bytes, Base64.URL_SAFE);
    }

    /**
     * Compresses the given string with gzip, returns it Base64 encoded and URL-safe.
     *
     * @param string input string
     * @return gzipped and Base64 encoded input string without new lines
     * @throws IOException IO Exception
     */
    static String gzipBase64UrlSafe(String string) throws IOException {
        String result = gzipBase64(string);

        if (!StringUtils.isEmpty(result)) {
            result = result.replaceAll("\n", "");
        }

        return result;
    }
}
