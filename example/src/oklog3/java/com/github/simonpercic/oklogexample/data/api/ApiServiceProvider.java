package com.github.simonpercic.oklogexample.data.api;

import android.support.annotation.NonNull;

import com.github.simonpercic.oklog3.OkLogInterceptor;
import com.github.simonpercic.oklogexample.Constants;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

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

        HttpLoggingInterceptor httpLoggingInterceptor = createHttpLoggingInterceptor();

        OkHttpClient okHttpClient = createOkHttpClient(okLogInterceptor, httpLoggingInterceptor);

        return new Retrofit.Builder()
                .baseUrl(Constants.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .client(okHttpClient)
                .build()
                .create(ApiService.class);
    }

    @NonNull private static HttpLoggingInterceptor createHttpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(Level.BODY);

        return httpLoggingInterceptor;
    }

    @NonNull private static OkHttpClient createOkHttpClient(Interceptor... interceptors) {
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();

        if (interceptors != null && interceptors.length > 0) {
            for (Interceptor interceptor : interceptors) {
                okHttpBuilder.addInterceptor(interceptor);
            }
        }

        return okHttpBuilder.build();
    }
}
