package com.github.simonpercic.oklog3;

import android.support.annotation.NonNull;

import com.github.simonpercic.oklog.core.LogDataBuilder;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor.Chain;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpEngine;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Log data interceptor util.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
final class LogDataInterceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private LogDataInterceptor() {
        // no instance
    }

    @NonNull static RequestLogData processRequest(Chain chain) throws IOException {
        LogDataBuilder logDataBuilder = new LogDataBuilder();

        Request request = chain.request();

        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        Connection connection = chain.connection();
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        logDataBuilder
                .requestMethod(request.method())
                .requestUrl(request.url().toString())
                .protocol(protocol.toString());

        if (hasRequestBody) {
            // Request body headers are only present when installed as a network interceptor. Force
            // them to be included (when available) so there values are known.
            if (requestBody.contentType() != null) {
                logDataBuilder.contentType(requestBody.contentType().toString());
            }

            if (requestBody.contentLength() != -1) {
                logDataBuilder.contentLength(requestBody.contentLength());
            }
        }

        Headers headers = request.headers();
        for (int i = 0, count = headers.size(); i < count; i++) {
            String name = headers.name(i);
            // Skip headers from the request body as they are explicitly logged above.
            if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                logDataBuilder.addRequestHeader(name, headers.value(i));
            }
        }

        if (!hasRequestBody) {
            logDataBuilder.requestBodyState(LogDataBuilder.NO_BODY);
        } else if (bodyEncoded(request.headers())) {
            logDataBuilder.requestBodyState(LogDataBuilder.ENCODED_BODY);
        } else {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);

            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }

            if (isPlaintext(buffer)) {
                logDataBuilder.requestBody(buffer.readString(charset));
            } else {
                logDataBuilder.requestBodyState(LogDataBuilder.BINARY_BODY);
            }
        }

        return new RequestLogData(request, logDataBuilder);
    }

    @NonNull
    static ResponseLogData processResponse(LogDataBuilder logDataBuilder, Response response) throws IOException {
        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        logDataBuilder
                .responseCode(response.code())
                .responseMessage(response.message())
                .responseContentLength(contentLength)
                .responseUrl(response.request().url().toString());

        Headers responseHeaders = response.headers();
        for (int i = 0, count = responseHeaders.size(); i < count; i++) {
            logDataBuilder.addResponseHeader(responseHeaders.name(i), responseHeaders.value(i));
        }

        if (!HttpEngine.hasBody(response)) {
            logDataBuilder.responseBodyState(LogDataBuilder.NO_BODY);
        } else if (bodyEncoded(response.headers())) {
            logDataBuilder.responseBodyState(LogDataBuilder.ENCODED_BODY);
        } else {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                try {
                    charset = contentType.charset(UTF8);
                } catch (UnsupportedCharsetException e) {
                    logDataBuilder.responseBodyState(LogDataBuilder.CHARSET_MALFORMED);
                    return new ResponseLogData(response, logDataBuilder);
                }
            }

            if (!isPlaintext(buffer)) {
                logDataBuilder.responseBodyState(LogDataBuilder.BINARY_BODY);
                logDataBuilder.responseBodySize(buffer.size());
                return new ResponseLogData(response, logDataBuilder);
            }

            if (contentLength != 0) {
                logDataBuilder.responseBody(buffer.clone().readString(charset));
            }

            logDataBuilder.responseBodySize(buffer.size());
        }

        return new ResponseLogData(response, logDataBuilder);
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    private static boolean isPlaintext(Buffer buffer) throws EOFException {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private static boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }

    static final class RequestLogData {

        private final Request request;
        private final LogDataBuilder logData;

        private RequestLogData(Request request, LogDataBuilder logData) {
            this.request = request;
            this.logData = logData;
        }

        Request getRequest() {
            return request;
        }

        LogDataBuilder getLogData() {
            return logData;
        }
    }

    static final class ResponseLogData {

        private final Response response;
        private final LogDataBuilder logData;

        private ResponseLogData(Response response, LogDataBuilder logData) {
            this.response = response;
            this.logData = logData;
        }

        Response getResponse() {
            return response;
        }

        LogDataBuilder getLogData() {
            return logData;
        }
    }
}
