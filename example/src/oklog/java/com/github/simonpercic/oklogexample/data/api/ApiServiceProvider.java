package com.github.simonpercic.oklogexample.data.api;

import android.support.annotation.NonNull;

import com.github.simonpercic.oklog.OkLogInterceptor;
import com.github.simonpercic.oklogexample.Constants;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;

import java.util.Collections;
import java.util.List;

import retrofit.Endpoints;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.OkClient;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public final class ApiServiceProvider {

    private ApiServiceProvider() {
        // no instance
    }

    /**
     * Creates an ApiService.
     * You should use Dagger DI to provide your ApiService, this pattern is used just to simplify the example app.
     *
     * @return ApiService
     */
    @NonNull public static ApiService createApiService() {
        OkLogInterceptor okLogInterceptor = OkLogInterceptor.builder().build();

        OkHttpClient okHttpClient = createOkHttpClient(okLogInterceptor);

        return new RestAdapter.Builder()
                .setClient(new OkClient(okHttpClient))
                .setEndpoint(Endpoints.newFixedEndpoint(Constants.ENDPOINT))
                .setLogLevel(LogLevel.FULL)
                .build()
                .create(ApiService.class);
    }

    @NonNull private static OkHttpClient createOkHttpClient(Interceptor... interceptors) {
        OkHttpClient client = new OkHttpClient();

        List<Interceptor> clientInterceptors = client.interceptors();
        Collections.addAll(clientInterceptors, interceptors);

        return client;
    }
}
