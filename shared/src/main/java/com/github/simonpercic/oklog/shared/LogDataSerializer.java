package com.github.simonpercic.oklog.shared;

import com.github.simonpercic.oklog.shared.data.LogData;

import java.nio.charset.Charset;

/**
 * Log data serializer.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public final class LogDataSerializer {

    private LogDataSerializer() {
        // no instance
    }

    public static String serialize(LogData logData) {
        if (logData == null) {
            return null;
        }

        byte[] bytes = LogData.ADAPTER.encode(logData);
        return new String(bytes, Charset.forName("UTF-8"));
    }
}
