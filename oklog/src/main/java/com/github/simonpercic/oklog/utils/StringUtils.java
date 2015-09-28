package com.github.simonpercic.oklog.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public final class StringUtils {

    private StringUtils() {

    }

    public static String gzipBase64(String string) throws IOException {
        if (TextUtils.isEmpty(string)) {
            return string;
        }

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream(string.length());

        GZIPOutputStream gzip = new GZIPOutputStream(byteOut);
        gzip.write(string.getBytes("UTF-8"));
        gzip.close();

        byte[] bytes = byteOut.toByteArray();

        byteOut.close();

        return Base64.encodeToString(bytes, Base64.URL_SAFE);
    }
}
