package com.github.simonpercic.oklog.core;

import android.support.annotation.Nullable;

import com.github.simonpercic.oklog.shared.data.BodyState;
import com.github.simonpercic.oklog.shared.data.HeaderData;
import com.github.simonpercic.oklog.shared.data.LogData;

import java.util.ArrayList;
import java.util.List;

/**
 * Log data converter.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
final class LogDataConverter {

    private LogDataConverter() {
        // no instance
    }

    @Nullable static LogData convert(@Nullable LogDataBuilder builder) {
        if (builder == null) {
            return null;
        }

        List<HeaderData> requestHeaders = convertHeaders(builder.getRequestHeaders());
        List<HeaderData> responseHeaders = convertHeaders(builder.getResponseHeaders());

        LogData.Builder logDataBuilder = new LogData.Builder()
                .request_method(builder.getRequestMethod())
                .request_url(builder.getRequestUrl())
                .protocol(builder.getProtocol())
                .request_content_type(builder.getRequestContentType())
                .request_content_length(builder.getResponseContentLength())
                .request_body_state(BodyState.fromValue(builder.getRequestBodyState()))
                .request_failed(builder.isRequestFailed())
                .response_code(builder.getResponseCode())
                .response_message(builder.getResponseMessage())
                .response_url(builder.getResponseUrl())
                .response_duration_ms(builder.getResponseDurationMs())
                .response_content_length(builder.getResponseContentLength())
                .response_body_state(BodyState.fromValue(builder.getResponseBodyState()))
                .response_body_size(builder.getResponseBodySize());

        if (requestHeaders != null) {
            logDataBuilder.request_headers(requestHeaders);
        }

        if (responseHeaders != null) {
            logDataBuilder.response_headers(responseHeaders);
        }

        return logDataBuilder.build();
    }

    @Nullable private static List<HeaderData> convertHeaders(@Nullable List<LogDataBuilder.HeaderDataBuilder> headers) {
        if (headers == null || headers.size() == 0) {
            return null;
        }

        List<HeaderData> result = new ArrayList<>();

        for (LogDataBuilder.HeaderDataBuilder header : headers) {
            HeaderData headerData = new HeaderData.Builder()
                    .name(header.getName())
                    .value(header.getValue())
                    .build();

            result.add(headerData);
        }

        return result;
    }
}
