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

        public Builder setLocal() {
            this.useLocal = true;
            return this;
        }

        public Builder setLogInterceptor(LogInterceptor logInterceptor) {
            this.logInterceptor = logInterceptor;
            return this;
        }

        public OkLogInterceptor build() {
            return new OkLogInterceptor(this.useLocal, this.logInterceptor);
        }
    }

    // endregion Builder
}
