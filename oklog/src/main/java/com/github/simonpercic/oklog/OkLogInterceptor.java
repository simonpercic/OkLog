package com.github.simonpercic.oklog;

import com.github.simonpercic.oklog.manager.LogManager;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public class OkLogInterceptor implements Interceptor {

    @Override public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        MediaType contentType = response.body().contentType();
        String bodyString = response.body().string();

        LogManager.log(bodyString);

        ResponseBody body = ResponseBody.create(contentType, bodyString);
        return response.newBuilder().body(body).build();
    }
}
