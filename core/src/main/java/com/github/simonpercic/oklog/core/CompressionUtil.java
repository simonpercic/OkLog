package com.github.simonpercic.oklog.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okio.BufferedSink;
import okio.ByteString;
import okio.GzipSink;
import okio.Okio;

/**
 * Compression util.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public class CompressionUtil {

    /**
     * Compresses the given string with gzip, returns it Base64 encoded.
     *
     * @param string input string
     * @return gzipped and Base64 encoded input string
     * @throws IOException IO Exception
     */
    private static String gzipBase64(String string) throws IOException {
        if (StringUtils.isEmpty(string)) {
            return string;
        }

        return gzipBase64(string.getBytes(Constants.CHARSET_UTF8));
    }

    /**
     * Compresses the given bytes with gzip, returns a Base64 encoded string.
     *
     * @param bytes input bytes
     * @return gzipped and Base64 encoded input string
     * @throws IOException IO Exception
     */
    private static String gzipBase64(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream(bytes.length);
        BufferedSink gzipSink = Okio.buffer(new GzipSink(Okio.sink(out)));
        gzipSink.write(bytes);

        gzipSink.close();

        byte[] result = out.toByteArray();

        out.close();

        ByteString byteString = ByteString.of(result);

        return byteString.base64Url();
    }

    /**
     * Compresses the given string with gzip, returns it Base64 encoded and URL-safe.
     *
     * @param string input string
     * @return gzipped and Base64 encoded input string without new lines
     * @throws IOException IO Exception
     */
    String gzipBase64UrlSafe(String string) throws IOException {
        String result = gzipBase64(string);

        if (!StringUtils.isEmpty(result)) {
            result = result.replaceAll("\n", "");
        }

        return result;
    }

    /**
     * Compresses the given bytes with gzip, returns a Base64 encoded and URL-safe string.
     *
     * @param bytes input bytes
     * @return gzipped and Base64 encoded input string without new lines
     * @throws IOException IO Exception
     */
    String gzipBase64UrlSafe(byte[] bytes) throws IOException {
        String result = gzipBase64(bytes);

        if (!StringUtils.isEmpty(result)) {
            result = result.replaceAll("\n", "");
        }

        return result;
    }
}
