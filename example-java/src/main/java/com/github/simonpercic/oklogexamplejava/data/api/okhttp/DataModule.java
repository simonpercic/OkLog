package com.github.simonpercic.oklogexamplejava.data.api.okhttp;

import com.github.simonpercic.oklog.OkLogInterceptor;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;

import java.util.Set;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import retrofit.Endpoints;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@Module
public class DataModule {

    private static final String ENDPOINT = "http://private-b7bc4-tvshowsapi.apiary-mock.com";

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(Set<Interceptor> interceptors) {
        OkHttpClient okHttpClient = new OkHttpClient();

        if (interceptors != null && interceptors.size() > 0) {
            okHttpClient.interceptors().addAll(interceptors);
        }

        return okHttpClient;
    }

    @Provides
    @Singleton
    public RestApi provideRestApi(OkHttpClient okHttpClient) {
        return new RestAdapter.Builder()
                .setClient(new OkClient(okHttpClient))
                .setEndpoint(Endpoints.newFixedEndpoint(ENDPOINT))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build()
                .create(RestApi.class);
    }

    @Provides
    @IntoSet
    @Singleton
    public Interceptor provideOkLogInterceptor() {
        return OkLogInterceptor.builder().build();
    }
}
