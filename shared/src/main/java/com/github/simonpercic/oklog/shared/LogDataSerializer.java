package com.github.simonpercic.oklog.shared;

import com.github.simonpercic.oklog.shared.data.LogData;

import java.io.IOException;

/**
 * Log data serializer.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public final class LogDataSerializer {

    private LogDataSerializer() {
        // no instance
    }

    public static byte[] serialize(LogData logData) {
        if (logData == null) {
            return null;
        }

        return LogData.ADAPTER.encode(logData);
    }

    public static LogData deserialize(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        return LogData.ADAPTER.decode(bytes);
    }
}
