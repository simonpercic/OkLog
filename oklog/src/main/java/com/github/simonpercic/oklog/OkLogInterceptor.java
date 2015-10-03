package com.github.simonpercic.oklog;

import com.github.simonpercic.oklog.manager.LogManager;
import com.github.simonpercic.oklog.utils.Constants;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

/**
 * Interceptor for OkHttp.
 * Call builder() to create an instance.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public final class OkLogInterceptor implements Interceptor {

    private final LogManager logManager;

    private OkLogInterceptor(boolean useLocal, LogInterceptor logInterceptor) {
        String logUrlBase = useLocal ? Constants.LOG_URL_BASE_LOCAL : Constants.LOG_URL_BASE_REMOTE;
        this.logManager = new LogManager(logUrlBase, logInterceptor);
    }

    @Override public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        MediaType contentType = response.body().contentType();
        String bodyString = response.body().string();

        logManager.log(bodyString);

        ResponseBody body = ResponseBody.create(contentType, bodyString);
        return response.newBuilder().body(body).build();
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

        private boolean useLocal;
        private LogInterceptor logInterceptor;

        private Builder() {
            this.useLocal = false;
        }

        /**
         * Set the base url to 'http://localhost:8080'.
         *
         * @return Builder instance, to chain calls
         */
        public Builder setLocal() {
            this.useLocal = true;
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
            return new OkLogInterceptor(this.useLocal, this.logInterceptor);
        }
    }

    // endregion Builder
}
