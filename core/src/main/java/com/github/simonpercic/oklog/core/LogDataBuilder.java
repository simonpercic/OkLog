package com.github.simonpercic.oklog.core;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Log data builder.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public class LogDataBuilder {

    // region BodyState IntDef

    @IntDef({PLAIN_BODY, NO_BODY, ENCODED_BODY, BINARY_BODY, CHARSET_MALFORMED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BodyState {
    }

    public static final int PLAIN_BODY = 0;
    public static final int NO_BODY = 1;
    public static final int ENCODED_BODY = 2;
    public static final int BINARY_BODY = 3;
    public static final int CHARSET_MALFORMED = 4;

    // endregion BodyState IntDef

    private String requestMethod;
    private String requestUrl;
    private String protocol;
    private String requestContentType;
    private long requestContentLength;
    private List<HeaderData> requestHeaders;
    private String requestBody;
    @BodyState private int requestBodyState;
    private boolean requestFailed;

    private int responseCode;
    private String responseMessage;
    private String responseUrl;
    private long responseDurationMs;
    private long responseContentLength;
    private List<HeaderData> responseHeaders;
    @BodyState private int responseBodyState;
    private long responseBodySize;
    private String responseBody;

    public LogDataBuilder() {
        this.requestBodyState = PLAIN_BODY;
        this.responseBodyState = PLAIN_BODY;
    }

    public LogDataBuilder requestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    public LogDataBuilder requestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
        return this;
    }

    public LogDataBuilder protocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public LogDataBuilder contentType(String contentType) {
        this.requestContentType = contentType;
        return this;
    }

    public LogDataBuilder contentLength(long contentLength) {
        this.requestContentLength = contentLength;
        return this;
    }

    public LogDataBuilder addRequestHeader(String name, String value) {
        if (this.requestHeaders == null) {
            this.requestHeaders = new ArrayList<>();
        }

        this.requestHeaders.add(new HeaderData(name, value));
        return this;
    }

    public LogDataBuilder requestBody(String requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    public LogDataBuilder requestBodyState(@BodyState int requestBodyState) {
        this.requestBodyState = requestBodyState;
        return this;
    }

    public LogDataBuilder requestFailed() {
        this.requestFailed = true;
        return this;
    }

    public LogDataBuilder responseCode(int responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    public LogDataBuilder responseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
        return this;
    }

    public LogDataBuilder responseUrl(String responseUrl) {
        this.responseUrl = responseUrl;
        return this;
    }

    public LogDataBuilder responseDurationMs(long responseDurationMs) {
        this.responseDurationMs = responseDurationMs;
        return this;
    }

    public LogDataBuilder responseContentLength(long responseContentLength) {
        this.responseContentLength = responseContentLength;
        return this;
    }

    public LogDataBuilder addResponseHeader(String name, String value) {
        if (this.responseHeaders == null) {
            this.responseHeaders = new ArrayList<>();
        }

        this.responseHeaders.add(new HeaderData(name, value));
        return this;
    }

    public LogDataBuilder responseBodyState(@BodyState int responseBodyState) {
        this.responseBodyState = responseBodyState;
        return this;
    }

    public LogDataBuilder responseBodySize(long responseBodySize) {
        this.responseBodySize = responseBodySize;
        return this;
    }

    public LogDataBuilder responseBody(String responseBody) {
        this.responseBody = responseBody;
        return this;
    }

    // region Getters

    String getRequestMethod() {
        return requestMethod;
    }

    String getRequestUrl() {
        return requestUrl;
    }

    String getProtocol() {
        return protocol;
    }

    String getRequestContentType() {
        return requestContentType;
    }

    long getRequestContentLength() {
        return requestContentLength;
    }

    List<HeaderData> getRequestHeaders() {
        return requestHeaders;
    }

    String getRequestBody() {
        return requestBody;
    }

    int getRequestBodyState() {
        return requestBodyState;
    }

    boolean isRequestFailed() {
        return requestFailed;
    }

    int getResponseCode() {
        return responseCode;
    }

    String getResponseMessage() {
        return responseMessage;
    }

    String getResponseUrl() {
        return responseUrl;
    }

    long getResponseDurationMs() {
        return responseDurationMs;
    }

    long getResponseContentLength() {
        return responseContentLength;
    }

    List<HeaderData> getResponseHeaders() {
        return responseHeaders;
    }

    int getResponseBodyState() {
        return responseBodyState;
    }

    long getResponseBodySize() {
        return responseBodySize;
    }

    String getResponseBody() {
        return responseBody;
    }

    // endregion Getters

    static final class HeaderData {

        private final String name;
        private final String value;

        private HeaderData(String name, String value) {
            this.name = name;
            this.value = value;
        }

        String getName() {
            return name;
        }

        String getValue() {
            return value;
        }
    }
}
