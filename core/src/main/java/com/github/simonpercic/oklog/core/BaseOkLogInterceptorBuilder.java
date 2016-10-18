package com.github.simonpercic.oklog.core;

import android.support.annotation.NonNull;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public class BaseOkLogInterceptorBuilder {

    protected String logUrlBase;
    protected LogInterceptor logInterceptor;
    protected boolean useAndroidLog;
    protected boolean requestBody;

    private boolean requestMethod;
    private boolean requestUrl;
    private boolean protocol;
    private boolean requestContentType;
    private boolean requestContentLength;
    private boolean requestBodyState;
    private boolean requestHeaders;
    private boolean requestFailedState;
    private boolean responseCode;
    private boolean responseMessage;
    private boolean responseUrl;
    private boolean responseDuration;
    private boolean responseSize;
    private boolean responseBodyState;
    private boolean responseHeaders;

    protected BaseOkLogInterceptorBuilder() {
        this.logUrlBase = Constants.LOG_URL_BASE_REMOTE;
        this.requestBody = true;
        this.requestMethod = true;
        this.requestUrl = true;
        this.requestContentLength = true;
        this.requestBodyState = true;
        this.requestFailedState = true;
        this.responseCode = true;
        this.responseMessage = true;
        this.responseDuration = true;
        this.responseSize = true;
        this.responseBodyState = true;
    }

    protected void baseSetBaseUrl(String url) {
        if (!StringUtils.isEmpty(url)) {
            this.logUrlBase = url;
        }
    }

    protected void baseUseAndroidLog(boolean useAndroidLog) {
        this.useAndroidLog = useAndroidLog;
    }

    protected void baseSetLogInterceptor(LogInterceptor logInterceptor) {
        this.logInterceptor = logInterceptor;
    }

    protected void baseWithRequestBody(boolean requestBody) {
        this.requestBody = requestBody;
    }

    protected void baseWithRequestMethod(boolean requestMethod) {
        this.requestMethod = requestMethod;
    }

    protected void baseWithRequestUrl(boolean requestUrl) {
        this.requestUrl = requestUrl;
    }

    protected void baseWithProtocol(boolean protocol) {
        this.protocol = protocol;
    }

    protected void baseWithRequestContentType(boolean requestContentType) {
        this.requestContentType = requestContentType;
    }

    protected void baseWithRequestContentLength(boolean requestContentLength) {
        this.requestContentLength = requestContentLength;
    }

    protected void baseWithRequestBodyState(boolean requestBodyState) {
        this.requestBodyState = requestBodyState;
    }

    protected void baseWithRequestHeaders(boolean requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    protected void baseWithRequestFailedState(boolean requestFailedState) {
        this.requestFailedState = requestFailedState;
    }

    protected void baseWithResponseCode(boolean responseCode) {
        this.responseCode = responseCode;
    }

    protected void baseWithResponseMessage(boolean responseMessage) {
        this.responseMessage = responseMessage;
    }

    protected void baseWithResponseUrl(boolean responseUrl) {
        this.responseUrl = responseUrl;
    }

    protected void baseWithResponseDuration(boolean responseDuration) {
        this.responseDuration = responseDuration;
    }

    protected void baseWithResponseSize(boolean responseSize) {
        this.responseSize = responseSize;
    }

    protected void baseWithResponseBodyState(boolean responseBodyState) {
        this.responseBodyState = responseBodyState;
    }

    protected void baseWithResponseHeaders(boolean responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    protected void baseWithNoLogData() {
        setLogData(false);
    }

    protected void baseWithAllLogData() {
        setLogData(true);
    }

    private void setLogData(boolean value) {
        this.requestBody = value;
        this.requestMethod = value;
        this.requestUrl = value;
        this.protocol = value;
        this.requestContentType = value;
        this.requestContentLength = value;
        this.requestBodyState = value;
        this.requestHeaders = value;
        this.requestFailedState = value;
        this.responseCode = value;
        this.responseMessage = value;
        this.responseUrl = value;
        this.responseDuration = value;
        this.responseSize = value;
        this.responseBodyState = value;
        this.responseHeaders = value;
    }

    @NonNull protected LogDataConfig buildLogDataConfig() {
        return new LogDataConfig(
                requestMethod,
                requestUrl,
                protocol,
                requestContentType,
                requestContentLength,
                requestBodyState,
                requestHeaders,
                requestFailedState,
                responseCode,
                responseMessage,
                responseUrl,
                responseDuration,
                responseSize,
                responseBodyState,
                responseHeaders);
    }
}
