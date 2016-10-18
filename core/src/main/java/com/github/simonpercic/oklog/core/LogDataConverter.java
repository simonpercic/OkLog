package com.github.simonpercic.oklog.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.simonpercic.collectionhelper.CollectionHelper;
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

    @Nullable static LogData convert(@Nullable LogDataBuilder builder, @NonNull LogDataConfig config) {
        if (builder == null) {
            return null;
        }

        if (!config.any()) {
            return null;
        }

        List<HeaderData> requestHeaders = config.requestHeaders ? convertHeaders(builder.getRequestHeaders()) : null;
        List<HeaderData> responseHeaders = config.responseHeaders ? convertHeaders(builder.getResponseHeaders()) : null;

        LogData.Builder logDataBuilder = new LogData.Builder();

        if (config.requestMethod) {
            logDataBuilder.request_method(builder.getRequestMethod());
        }

        if (config.requestUrl) {
            logDataBuilder.request_url(builder.getRequestUrl());
        }

        if (config.protocol) {
            logDataBuilder.protocol(builder.getProtocol());
        }

        if (config.requestContentType) {
            logDataBuilder.request_content_type(builder.getRequestContentType());
        }

        if (config.requestContentLength) {
            logDataBuilder.request_content_length(builder.getRequestContentLength());
        }

        if (config.requestBodyState) {
            logDataBuilder.request_body_state(BodyState.fromValue(builder.getRequestBodyState()));
        }

        if (config.requestFailedState) {
            logDataBuilder.request_failed(builder.isRequestFailed());
        }

        if (config.responseCode) {
            logDataBuilder.response_code(builder.getResponseCode());
        }

        if (config.responseMessage) {
            logDataBuilder.response_message(builder.getResponseMessage());
        }

        if (config.responseUrl) {
            logDataBuilder.response_url(builder.getResponseUrl());
        }

        if (config.responseDuration) {
            logDataBuilder.response_duration_ms(builder.getResponseDurationMs());
        }

        if (config.responseBodyState) {
            logDataBuilder.response_body_state(BodyState.fromValue(builder.getResponseBodyState()));
        }

        if (config.responseSize) {
            logDataBuilder.response_content_length(builder.getResponseContentLength());
            logDataBuilder.response_body_size(builder.getResponseBodySize());
        }

        if (requestHeaders != null) {
            logDataBuilder.request_headers(requestHeaders);
        }

        if (responseHeaders != null) {
            logDataBuilder.response_headers(responseHeaders);
        }

        return logDataBuilder.build();
    }

    @Nullable private static List<HeaderData> convertHeaders(@Nullable List<LogDataBuilder.HeaderDataBuilder> headers) {
        if (CollectionHelper.isEmpty(headers)) {
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
