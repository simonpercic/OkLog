package com.github.simonpercic.oklog3;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */

import com.github.simonpercic.oklog.core.LogDataBuilder;
import com.github.simonpercic.oklog.core.LogInterceptor;
import com.github.simonpercic.oklog.core.LogManager;
import com.github.simonpercic.oklog.core.Constants;
import com.github.simonpercic.oklog.core.StringUtils;
import com.github.simonpercic.oklog3.LogDataInterceptor.RequestLogData;
import com.github.simonpercic.oklog3.LogDataInterceptor.ResponseLogData;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Interceptor for OkHttp3.
 * Call builder() to create an instance.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public final class OkLogInterceptor implements Interceptor {

    private final LogManager logManager;

    private OkLogInterceptor(String logUrlBase, LogInterceptor logInterceptor, boolean useAndroidLog) {
        this.logManager = new LogManager(logUrlBase, logInterceptor, useAndroidLog);
    }

    @Override public Response intercept(Chain chain) throws IOException {
        RequestLogData requestLogData = LogDataInterceptor.processRequest(chain);
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

        ResponseLogData responseLogData = LogDataInterceptor.processResponse(logDataBuilder, response);
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

    public static final class Builder {

        private String logUrlBase;
        private LogInterceptor logInterceptor;
        private boolean useAndroidLog;

        private Builder() {
            this.logUrlBase = Constants.LOG_URL_BASE_REMOTE;
        }

        /**
         * Set the base url to 'http://localhost:8080'.
         *
         * @return Builder instance, to chain calls
         * @deprecated You can pass "http://localhost:8080" to {@link #setBaseUrl(String)} to achieve the same result.
         */
        @Deprecated
        public Builder setLocal() {
            return setBaseUrl(Constants.LOG_URL_BASE_LOCAL);
        }

        /**
         * Set the base url.
         * The set url should be without the trailing slash.
         * e.g. http://www.example.com
         *
         * @param url base url
         * @return Builder instance, to chain calls
         */
        public Builder setBaseUrl(String url) {
            if (StringUtils.isEmpty(url)) {
                return this;
            }

            this.logUrlBase = url;
            return this;
        }

        /**
         * Set whether to use Android's Log methods to log, instead of Timber.
         * Defaults to false.
         *
         * @param useAndroidLog true to use Android's Log methods, false to use Timber.
         * @return Builder instance, to chain calls
         */
        public Builder useAndroidLog(boolean useAndroidLog) {
            this.useAndroidLog = useAndroidLog;
            return this;
        }

        /**
         * Set a custom LogInterceptor.
         *
         * @param logInterceptor a instance of a LogInterceptor
         * @return Builder instance, to chain calls
         */
        public Builder setLogInterceptor(LogInterceptor logInterceptor) {
            this.logInterceptor = logInterceptor;
            return this;
        }

        /**
         * Build an instance of OkLogInterceptor.
         *
         * @return instance of OkLogInterceptor
         */
        public OkLogInterceptor build() {
            return new OkLogInterceptor(this.logUrlBase, this.logInterceptor, this.useAndroidLog);
        }
    }

    // endregion Builder
}
