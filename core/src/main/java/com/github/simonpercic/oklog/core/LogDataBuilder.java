package com.github.simonpercic.oklog.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Log data builder.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public class LogDataBuilder {

    // region BodyState

    public enum BodyState {

        PLAIN_BODY(1),
        NO_BODY(2),
        ENCODED_BODY(3),
        BINARY_BODY(4),
        CHARSET_MALFORMED(5);

        private final int intValue;

        BodyState(int intValue) {
            this.intValue = intValue;
        }

        int getIntValue() {
            return intValue;
        }
    }

    // endregion BodyState

    private String requestMethod;
    private String requestUrl;
    private String requestUrlPath;
    private String protocol;
    private String requestContentType;
    private long requestContentLength;
    private List<HeaderDataBuilder> requestHeaders;
    private String requestBody;
    private BodyState requestBodyState;
    private boolean requestFailed;

    private int responseCode;
    private String responseMessage;
    private String responseUrl;
    private long responseDurationMs;
    private long responseContentLength;
    private List<HeaderDataBuilder> responseHeaders;
    private BodyState responseBodyState;
    private long responseBodySize;
    private String responseBody;

    LogDataBuilder() {
        this.requestBodyState = BodyState.PLAIN_BODY;
        this.responseBodyState = BodyState.PLAIN_BODY;
    }

    LogDataBuilder requestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    LogDataBuilder requestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
        return this;
    }

    LogDataBuilder requestUrlPath(String requestUrlPath) {
        this.requestUrlPath = requestUrlPath;
        return this;
    }

    LogDataBuilder protocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    LogDataBuilder requestContentType(String contentType) {
        this.requestContentType = contentType;
        return this;
    }

    LogDataBuilder requestContentLength(long contentLength) {
        this.requestContentLength = contentLength;
        return this;
    }

    LogDataBuilder addRequestHeader(String name, String value) {
        if (this.requestHeaders == null) {
            this.requestHeaders = new ArrayList<>();
        }

        this.requestHeaders.add(new HeaderDataBuilder(name, value));
        return this;
    }

    LogDataBuilder requestBody(String requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    LogDataBuilder requestBodyState(BodyState requestBodyState) {
        this.requestBodyState = requestBodyState;
        return this;
    }

    public LogDataBuilder requestFailed() {
        this.requestFailed = true;
        return this;
    }

    LogDataBuilder responseCode(int responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    LogDataBuilder responseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
        return this;
    }

    LogDataBuilder responseUrl(String responseUrl) {
        this.responseUrl = responseUrl;
        return this;
    }

    public LogDataBuilder responseDurationMs(long responseDurationMs) {
        this.responseDurationMs = responseDurationMs;
        return this;
    }

    LogDataBuilder responseContentLength(long responseContentLength) {
        this.responseContentLength = responseContentLength;
        return this;
    }

    LogDataBuilder addResponseHeader(String name, String value) {
        if (this.responseHeaders == null) {
            this.responseHeaders = new ArrayList<>();
        }

        this.responseHeaders.add(new HeaderDataBuilder(name, value));
        return this;
    }

    LogDataBuilder responseBodyState(BodyState responseBodyState) {
        this.responseBodyState = responseBodyState;
        return this;
    }

    LogDataBuilder responseBodySize(long responseBodySize) {
        this.responseBodySize = responseBodySize;
        return this;
    }

    LogDataBuilder responseBody(String responseBody) {
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

    String getRequestUrlPath() {
        return requestUrlPath;
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

    List<HeaderDataBuilder> getRequestHeaders() {
        return requestHeaders;
    }

    String getRequestBody() {
        return requestBody;
    }

    BodyState getRequestBodyState() {
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

    List<HeaderDataBuilder> getResponseHeaders() {
        return responseHeaders;
    }

    BodyState getResponseBodyState() {
        return responseBodyState;
    }

    long getResponseBodySize() {
        return responseBodySize;
    }

    String getResponseBody() {
        return responseBody;
    }

    // endregion Getters

    @Override public String toString() {
        String requestHeadersString = "";
        if (requestHeaders != null) {
            for (HeaderDataBuilder requestHeader : requestHeaders) {
                requestHeadersString += requestHeader.toString() + " ";
            }
        }

        String responseHeadersString = "";
        if (responseHeaders != null) {
            for (HeaderDataBuilder responseHeader : responseHeaders) {
                responseHeadersString += responseHeader.toString() + " ";
            }
        }

        return "LogDataBuilder{"
                + "\n" + "requestMethod='" + requestMethod + '\''
                + "\n" + ", requestUrl='" + requestUrl + '\''
                + "\n" + ", requestUrlPath='" + requestUrlPath + '\''
                + "\n" + ", protocol='" + protocol + '\''
                + "\n" + ", requestContentType='" + requestContentType + '\''
                + "\n" + ", requestContentLength=" + requestContentLength
                + "\n" + ", requestHeaders=" + requestHeadersString
                + "\n" + ", requestBody='" + requestBody + '\''
                + "\n" + ", requestBodyState=" + requestBodyState
                + "\n" + ", requestFailed=" + requestFailed
                + "\n" + ", responseCode=" + responseCode
                + "\n" + ", responseMessage='" + responseMessage + '\''
                + "\n" + ", responseUrl='" + responseUrl + '\''
                + "\n" + ", responseDurationMs=" + responseDurationMs
                + "\n" + ", responseContentLength=" + responseContentLength
                + "\n" + ", responseHeaders=" + responseHeadersString
                + "\n" + ", responseBodyState=" + responseBodyState
                + "\n" + ", responseBodySize=" + responseBodySize
                + "\n" + ", responseBody='" + responseBody + '\''
                + "\n" + '}';
    }

    static final class HeaderDataBuilder {

        private final String name;
        private final String value;

        private HeaderDataBuilder(String name, String value) {
            this.name = name;
            this.value = value;
        }

        String getName() {
            return name;
        }

        String getValue() {
            return value;
        }

        @Override public String toString() {
            return "HeaderDataBuilder{"
                    + "name='" + name + '\''
                    + ", value='" + value + '\''
                    + '}';
        }
    }
}
