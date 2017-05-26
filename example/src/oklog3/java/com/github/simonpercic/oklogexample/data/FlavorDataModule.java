package com.github.simonpercic.oklogexample.data;

import com.github.simonpercic.oklog3.OkLogInterceptor;
import com.github.simonpercic.oklogexample.Constants;
import com.github.simonpercic.oklogexample.data.api.ApiService;
import com.github.simonpercic.oklogexample.data.api.RestApi;
import com.github.simonpercic.oklogexample.data.api.RestApiService;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@Module
public class FlavorDataModule {

    @Provides @Singleton ApiService provideApiService(RestApiService restApiService) {
        return restApiService;
    }

    @Provides @Singleton RestApi provideRestApi(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(Constants.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .client(okHttpClient)
                .build()
                .create(RestApi.class);
    }

    @Provides @IntoSet @Singleton Interceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return httpLoggingInterceptor;
    }

    @Provides @IntoSet @Singleton Interceptor provideOkLogInterceptor() {
        return OkLogInterceptor.builder().build();
    }

    @Provides @Singleton OkHttpClient provideOkHttpClient(Set<Interceptor> interceptors) {
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();

        if (interceptors != null && interceptors.size() > 0) {
            for (Interceptor interceptor : interceptors) {
                okHttpBuilder.addInterceptor(interceptor);
            }
        }

        okHttpBuilder = okHttpBuilder
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES);

        return okHttpBuilder.build();
    }
}
