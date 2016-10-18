package com.github.simonpercic.oklog.core;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public class LogDataConfig {

    final boolean requestMethod;
    final boolean requestUrl;
    final boolean protocol;
    final boolean requestContentType;
    final boolean requestContentLength;
    final boolean requestBodyState;
    final boolean requestHeaders;
    final boolean requestFailedState;
    final boolean responseCode;
    final boolean responseMessage;
    final boolean responseUrl;
    final boolean responseDuration;
    final boolean responseSize;
    final boolean responseBodyState;
    final boolean responseHeaders;

    LogDataConfig(boolean requestMethod, boolean requestUrl, boolean protocol,
            boolean requestContentType, boolean requestContentLength, boolean requestBodyState, boolean requestHeaders,
            boolean requestFailedState, boolean responseCode, boolean responseMessage, boolean responseUrl,
            boolean responseDuration, boolean responseSize, boolean responseBodyState, boolean responseHeaders) {
        this.requestMethod = requestMethod;
        this.requestUrl = requestUrl;
        this.protocol = protocol;
        this.requestContentType = requestContentType;
        this.requestContentLength = requestContentLength;
        this.requestBodyState = requestBodyState;
        this.requestHeaders = requestHeaders;
        this.requestFailedState = requestFailedState;
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.responseUrl = responseUrl;
        this.responseDuration = responseDuration;
        this.responseSize = responseSize;
        this.responseBodyState = responseBodyState;
        this.responseHeaders = responseHeaders;
    }

    boolean any() {
        return requestMethod
                || requestUrl
                || protocol
                || requestContentType
                || requestContentLength
                || requestBodyState
                || requestHeaders
                || requestFailedState
                || responseCode
                || responseMessage
                || responseUrl
                || responseDuration
                || responseSize
                || responseBodyState
                || responseHeaders;
    }
}
