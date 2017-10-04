package com.github.simonpercic.oklog3;

import com.github.simonpercic.oklog.core.BaseLogDataInterceptor.RequestLogData;
import com.github.simonpercic.oklog.core.BaseLogDataInterceptor.ResponseLogData;
import com.github.simonpercic.oklog.core.BaseOkLogInterceptorBuilder;
import com.github.simonpercic.oklog.core.CompressionUtils;
import com.github.simonpercic.oklog.core.LogDataBuilder;
import com.github.simonpercic.oklog.core.LogDataConfig;
import com.github.simonpercic.oklog.core.LogInterceptor;
import com.github.simonpercic.oklog.core.LogManager;
import com.github.simonpercic.oklog.core.Logger;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Interceptor for OkHttp3.
 * Call builder() to create an instance.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public final class OkLogInterceptor implements Interceptor {

    private final LogManager logManager;
    private final LogDataInterceptor logDataInterceptor;

    private OkLogInterceptor(String logUrlBase, LogInterceptor logInterceptor, Logger logger, boolean ignoreTimber,
            boolean withRequestBody, boolean shortenInfoUrl, @NotNull LogDataConfig logDataConfig) {
        this(new LogManager(logUrlBase, logInterceptor, logger, ignoreTimber, withRequestBody, shortenInfoUrl,
                logDataConfig, new CompressionUtils()));
    }

    OkLogInterceptor(LogManager logManager) {
        this.logManager = logManager;
        this.logDataInterceptor = new LogDataInterceptor();
    }

    @Override public Response intercept(Chain chain) throws IOException {
        RequestLogData<Request> requestLogData = logDataInterceptor.processRequest(chain);
        LogDataBuilder logDataBuilder = requestLogData.getLogData();

        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(requestLogData.getRequest());
        } catch (Exception e) {
            logDataBuilder.requestFailed();
            logManager.log(logDataBuilder);
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        logDataBuilder.responseDurationMs(tookMs);

        ResponseLogData<Response> responseLogData = logDataInterceptor.processResponse(logDataBuilder, response);
        logManager.log(responseLogData.getLogData());

        return responseLogData.getResponse();
    }

    /**
     * Get a Builder instance.
     *
     * @return a Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    // region Builder

    public static final class Builder extends BaseOkLogInterceptorBuilder {

        /**
         * Set the base url.
         * The set url should be without the trailing slash.
         * e.g. http://www.example.com
         *
         * @param url base url
         * @return Builder instance, to chain calls
         */
        public Builder setBaseUrl(String url) {
            baseSetBaseUrl(url);
            return this;
        }

        /**
         * Set whether to ignore Timber for logging, even if it is present.
         * Defaults to false.
         *
         * @param ignoreTimber true to ignore Timber for logging, even if it is present.
         * @return Builder instance, to chain calls
         */
        public Builder ignoreTimber(boolean ignoreTimber) {
            baseIgnoreTimber(ignoreTimber);
            return this;
        }

        /**
         * Set whether to use Android's Log methods to log, instead of Timber.
         * Defaults to false.
         * <p>
         *
         * @param useAndroidLog true to use Android's Log methods, false to use Timber.
         * @return Builder instance, to chain calls
         * @deprecated Use {@link #ignoreTimber(boolean)} method instead.
         */
        @Deprecated
        public Builder useAndroidLog(boolean useAndroidLog) {
            ignoreTimber(!useAndroidLog);
            return this;
        }

        /**
         * Set a custom LogInterceptor.
         *
         * @param logInterceptor a instance of a LogInterceptor
         * @return Builder instance, to chain calls
         */
        public Builder setLogInterceptor(LogInterceptor logInterceptor) {
            baseSetLogInterceptor(logInterceptor);
            return this;
        }

        /**
         * Set a custom Logger.
         *
         * @param logger a instance of a Logger
         * @return Builder instance, to chain calls
         */
        public Builder setLogger(Logger logger) {
            baseLogger(logger);
            return this;
        }

        /**
         * Include request body.
         *
         * @param requestBody true to include, false to skip
         * @return Builder instance, to chain calls
         */
        public Builder withRequestBody(boolean requestBody) {
            baseWithRequestBody(requestBody);
            return this;
        }

        /**
         * Shorten info url on the server-side.
         *
         * @param shortenInfoUrl true to shorten url, false otherwise
         * @return Builder instance, to chain calls
         */
        public Builder shortenInfoUrl(boolean shortenInfoUrl) {
            baseShortenInfoUrl(shortenInfoUrl);
            return this;
        }

        /**
         * Include request method.
         *
         * @param requestMethod true to include, false to skip
         * @return Builder instance, to chain calls
         */
        public Builder withRequestMethod(boolean requestMethod) {
            baseWithRequestMethod(requestMethod);
            return this;
        }

        /**
         * Include request url.
         *
         * @param requestUrl true to include, false to skip
         * @return Builder instance, to chain calls
         */
        public Builder withRequestUrl(boolean requestUrl) {
            baseWithRequestUrl(requestUrl);
            return this;
        }

        /**
         * Include protocol.
         *
         * @param protocol true to include, false to skip
         * @return Builder instance, to chain calls
         */
        public Builder withProtocol(boolean protocol) {
            baseWithProtocol(protocol);
            return this;
        }

        /**
         * Include request content type.
         *
         * @param requestContentType true to include, false to skip
         * @return Builder instance, to chain calls
         */
        public Builder withRequestContentType(boolean requestContentType) {
            baseWithRequestContentType(requestContentType);
            return this;
        }

        /**
         * Include request content length.
         *
         * @param requestContentLength true to include, false to skip
         * @return Builder instance, to chain calls
         */
        public Builder withRequestContentLength(boolean requestContentLength) {
            baseWithRequestContentLength(requestContentLength);
            return this;
        }

        /**
         * Include request body state.
         *
         * @param requestBodyState true to include, false to skip
         * @return Builder instance, to chain calls
         */
        public Builder withRequestBodyState(boolean requestBodyState) {
            baseWithRequestBodyState(requestBodyState);
            return this;
        }

        /**
         * Include request headers.
         *
         * @param requestHeaders true to include, false to skip
         * @return Builder instance, to chain calls
         */
        public Builder withRequestHeaders(boolean requestHeaders) {
            baseWithRequestHeaders(requestHeaders);
            return this;
        }

        /**
         * Include request failed state.
         *
         * @param requestFailedState true to include, false to skip
         * @return Builder instance, to chain calls
         */
        public Builder withRequestFailedState(boolean requestFailedState) {
            baseWithRequestFailedState(requestFailedState);
            return this;
        }

        /**
         * Include response code.
         *
         * @param responseCode true to include, false to skip
         * @return Builder instance, to chain calls
         */
        public Builder withResponseCode(boolean responseCode) {
            baseWithResponseCode(responseCode);
            return this;
        }

        /**
         * Include response message.
         *
         * @param responseMessage true to include, false to skip
         * @return Builder instance, to chain calls
         */
        public Builder withResponseMessage(boolean responseMessage) {
            baseWithResponseMessage(responseMessage);
            return this;
        }

        /**
         * Include response url.
         *
         * @param responseUrl true to include, false to skip
         * @return Builder instance, to chain calls
         */
        public Builder withResponseUrl(boolean responseUrl) {
            baseWithResponseUrl(responseUrl);
            return this;
        }

        /**
         * Include response duration.
         *
         * @param responseDuration true to include, false to skip
         * @return Builder instance, to chain calls
         */
        public Builder withResponseDuration(boolean responseDuration) {
            baseWithResponseDuration(responseDuration);
            return this;
        }

        /**
         * Include response size.
         *
         * @param responseSize true to include, false to skip
         * @return Builder instance, to chain calls
         */
        public Builder withResponseSize(boolean responseSize) {
            baseWithResponseSize(responseSize);
            return this;
        }

        /**
         * Include response body state.
         *
         * @param responseBodyState true to include, false to skip
         * @return Builder instance, to chain calls
         */
        public Builder withResponseBodyState(boolean responseBodyState) {
            baseWithResponseBodyState(responseBodyState);
            return this;
        }

        /**
         * Include response headers.
         *
         * @param responseHeaders true to include, false to skip
         * @return Builder instance, to chain calls
         */
        public Builder withResponseHeaders(boolean responseHeaders) {
            baseWithResponseHeaders(responseHeaders);
            return this;
        }

        /**
         * Don't include any additional log data.
         *
         * @return Builder instance, to chain calls
         */
        public Builder withNoLogData() {
            baseWithNoLogData();
            return this;
        }

        /**
         * Include all additional log data.
         *
         * @return Builder instance, to chain calls
         */
        public Builder withAllLogData() {
            baseWithAllLogData();
            return this;
        }

        /**
         * Build an instance of OkLogInterceptor.
         *
         * @return instance of OkLogInterceptor
         */
        public OkLogInterceptor build() {
            return new OkLogInterceptor(logUrlBase, logInterceptor, logger, ignoreTimber, requestBody, shortenInfoUrl,
                    buildLogDataConfig());
        }
    }

    // endregion Builder
}
