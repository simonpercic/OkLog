package com.github.simonpercic.oklogexamplejava.data.api.okhttp3;

import com.github.simonpercic.oklog3.OkLogInterceptor;

import java.util.Set;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@Module
public class DataModule3 {

    private static final String ENDPOINT = "http://private-b7bc4-tvshowsapi.apiary-mock.com";

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(Set<Interceptor> interceptors)  {
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();

        if (interceptors != null && interceptors.size() > 0) {
            for (Interceptor interceptor : interceptors) {
                okHttpBuilder.addInterceptor(interceptor);
            }
        }

        return okHttpBuilder.build();
    }

    @Provides
    @Singleton
    public RestApi3 provideRestApi(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
                .create(RestApi3.class);
    }

    @Provides
    @IntoSet
    @Singleton
    public Interceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return httpLoggingInterceptor;
    }

    @Provides
    @IntoSet
    @Singleton
    public Interceptor provideOkLogInterceptor() {
        return OkLogInterceptor.builder().build();
    }
}
