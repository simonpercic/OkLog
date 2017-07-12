package com.github.simonpercic.oklog.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;

import okio.Buffer;
import okio.BufferedSource;

/**
 * Base log data interceptor util. Inspired by: https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public abstract class BaseLogDataInterceptor<Chain, Request, Response, Headers, MediaType> {

    private static final String CONTENT_ENCODING = "Content-Encoding";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String IDENTITY = "identity";

    // region Abstract methods

    protected abstract Request request(Chain chain);

    protected abstract String protocol(Chain chain);

    protected abstract String requestMethod(Request request);

    protected abstract String requestUrl(Request request);

    protected abstract String requestUrlPath(Request request);

    protected abstract String responseUrl(Response response);

    protected abstract Headers requestHeaders(Request request);

    protected abstract Headers responseHeaders(Response response);

    protected abstract int headersCount(Headers headers);

    protected abstract String headerName(Headers headers, int index);

    protected abstract String headerValue(Headers headers, int index);

    protected abstract String headerValue(Headers headers, String name);

    protected abstract boolean hasRequestBody(Request request);

    protected abstract boolean hasResponseBody(Response response);

    protected abstract int responseCode(Response response);

    protected abstract String responseMessage(Response response);

    protected abstract long requestContentLength(Request request) throws IOException;

    protected abstract long responseContentLength(Response response) throws IOException;

    protected abstract MediaType requestContentType(Request request);

    protected abstract MediaType responseContentType(Response response);

    protected abstract String contentTypeString(MediaType mediaType);

    protected abstract Charset contentTypeCharset(MediaType mediaType, Charset charset);

    @Nullable protected abstract Charset responseContentTypeCharset(MediaType contentType, Charset charset);

    protected abstract void writeRequestBody(Request request, Buffer buffer) throws IOException;

    protected abstract BufferedSource responseBodySource(Response response) throws IOException;

    // endregion Abstract methods

    @NonNull
    public RequestLogData<Request> processRequest(Chain chain) throws IOException {
        LogDataBuilder logDataBuilder = new LogDataBuilder();

        Request request = request(chain);

        boolean hasRequestBody = hasRequestBody(request);

        logDataBuilder
                .requestMethod(requestMethod(request))
                .requestUrl(requestUrl(request))
                .requestUrlPath(requestUrlPath(request))
                .protocol(protocol(chain));

        if (hasRequestBody) {
            // Request body headers are only present when installed as a network interceptor.
            // Explicitly include content type and content length.
            MediaType mediaType = requestContentType(request);
            if (mediaType != null) {
                logDataBuilder.requestContentType(contentTypeString(mediaType));
            }

            long contentLength = requestContentLength(request);
            logDataBuilder.requestContentLength(contentLength);
        }

        Headers headers = requestHeaders(request);
        for (int i = 0, count = headersCount(headers); i < count; i++) {
            String name = headerName(headers, i);
            // Skip headers from the request body as they are explicitly logged above.
            if (!CONTENT_TYPE.equalsIgnoreCase(name) && !CONTENT_LENGTH.equalsIgnoreCase(name)) {
                logDataBuilder.addRequestHeader(name, headerValue(headers, i));
            }
        }

        if (!hasRequestBody) {
            logDataBuilder.requestBodyState(LogDataBuilder.NO_BODY);
        } else if (bodyEncoded(headers)) {
            logDataBuilder.requestBodyState(LogDataBuilder.ENCODED_BODY);
        } else {
            Buffer buffer = new Buffer();
            writeRequestBody(request, buffer);

            Charset charset = Constants.CHARSET_UTF8;
            MediaType contentType = requestContentType(request);
            if (contentType != null) {
                charset = contentTypeCharset(contentType, Constants.CHARSET_UTF8);
            }

            if (isPlaintext(buffer)) {
                logDataBuilder.requestBody(buffer.readString(charset));
            } else {
                logDataBuilder.requestBodyState(LogDataBuilder.BINARY_BODY);
            }
        }

        return new RequestLogData<>(request, logDataBuilder);
    }

    @NonNull
    public ResponseLogData<Response> processResponse(LogDataBuilder logDataBuilder,
            Response response) throws IOException {

        long contentLength = responseContentLength(response);
        logDataBuilder
                .responseCode(responseCode(response))
                .responseMessage(responseMessage(response))
                .responseContentLength(contentLength)
                .responseUrl(responseUrl(response));

        Headers responseHeaders = responseHeaders(response);
        for (int i = 0, count = headersCount(responseHeaders); i < count; i++) {
            String name = headerName(responseHeaders, i);
            // skip content-length, since it's already logged above
            if (!CONTENT_LENGTH.equalsIgnoreCase(name) && !skipResponseHeader(name)) {
                logDataBuilder.addResponseHeader(name, headerValue(responseHeaders, i));
            }
        }

        if (!hasResponseBody(response)) {
            logDataBuilder.responseBodyState(LogDataBuilder.NO_BODY);
        } else if (bodyEncoded(responseHeaders)) {
            logDataBuilder.responseBodyState(LogDataBuilder.ENCODED_BODY);
        } else {
            BufferedSource source = responseBodySource(response);
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            Charset charset = Constants.CHARSET_UTF8;
            MediaType contentType = responseContentType(response);
            if (contentType != null) {
                charset = responseContentTypeCharset(contentType, Constants.CHARSET_UTF8);

                if (charset == null) {
                    logDataBuilder.responseBodyState(LogDataBuilder.CHARSET_MALFORMED);
                    return new ResponseLogData<>(response, logDataBuilder);
                }
            }

            if (!isPlaintext(buffer)) {
                logDataBuilder.responseBodyState(LogDataBuilder.BINARY_BODY);
                logDataBuilder.responseBodySize(buffer.size());
                return new ResponseLogData<>(response, logDataBuilder);
            }

            if (contentLength != 0) {
                logDataBuilder.responseBody(buffer.clone().readString(charset));
            }

            logDataBuilder.responseBodySize(buffer.size());
        }

        return new ResponseLogData<>(response, logDataBuilder);
    }

    protected boolean skipResponseHeader(String headerName) {
        return false;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    static boolean isPlaintext(Buffer buffer) throws EOFException {
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

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headerValue(headers, CONTENT_ENCODING);
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase(IDENTITY);
    }

    public static final class RequestLogData<Request> {

        private final Request request;
        private final LogDataBuilder logData;

        private RequestLogData(Request request, LogDataBuilder logData) {
            this.request = request;
            this.logData = logData;
        }

        public Request getRequest() {
            return request;
        }

        public LogDataBuilder getLogData() {
            return logData;
        }
    }

    public static final class ResponseLogData<Response> {

        private final Response response;
        private final LogDataBuilder logData;

        private ResponseLogData(Response response, LogDataBuilder logData) {
            this.response = response;
            this.logData = logData;
        }

        public Response getResponse() {
            return response;
        }

        public LogDataBuilder getLogData() {
            return logData;
        }
    }
}
