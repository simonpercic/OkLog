package com.github.simonpercic.oklogexample.data;

import com.github.simonpercic.oklog.OkLogInterceptor;
import com.github.simonpercic.oklogexample.Constants;
import com.github.simonpercic.oklogexample.data.api.ApiService;
import com.github.simonpercic.oklogexample.data.api.RestApi;
import com.github.simonpercic.oklogexample.data.api.RestApiService;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;

import java.util.List;
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
public class FlavorDataModule {

    @Provides @Singleton ApiService provideApiService(RestApiService restApiService) {
        return restApiService;
    }

    @Provides @Singleton RestApi provideRestApi(OkHttpClient okHttpClient) {
        return new RestAdapter.Builder()
                .setClient(new OkClient(okHttpClient))
                .setEndpoint(Endpoints.newFixedEndpoint(Constants.ENDPOINT))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build()
                .create(RestApi.class);
    }

    @Provides @IntoSet @Singleton Interceptor provideOkLogInterceptor() {
        return OkLogInterceptor.builder().build();
    }

    @Provides @Singleton OkHttpClient provideOkHttpClient(Set<Interceptor> interceptors) {
        OkHttpClient client = new OkHttpClient();

        List<Interceptor> clientInterceptors = client.interceptors();

        if (interceptors != null && interceptors.size() > 0) {
            for (Interceptor interceptor : interceptors) {
                clientInterceptors.add(interceptor);
            }
        }

        return client;
    }
}
