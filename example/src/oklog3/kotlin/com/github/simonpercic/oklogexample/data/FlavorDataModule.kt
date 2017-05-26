package com.github.simonpercic.oklogexample.data

import com.github.simonpercic.oklog3.OkLogInterceptor
import com.github.simonpercic.oklogexample.Constants
import com.github.simonpercic.oklogexample.data.api.ApiService
import com.github.simonpercic.oklogexample.data.api.RestApi
import com.github.simonpercic.oklogexample.data.api.RestApiService
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
@Module
class FlavorDataModule {

    @Provides @Singleton internal fun provideApiService(restApiService: RestApiService): ApiService {
        return restApiService
    }

    @Provides @Singleton internal fun provideRestApi(okHttpClient: OkHttpClient): RestApi {
        return Retrofit.Builder()
                .baseUrl(Constants.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .client(okHttpClient)
                .build()
                .create(RestApi::class.java)
    }

    @Provides @IntoSet @Singleton internal fun provideHttpLoggingInterceptor(): Interceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return httpLoggingInterceptor
    }

    @Provides @IntoSet @Singleton internal fun provideOkLogInterceptor(): Interceptor {
        return OkLogInterceptor.builder().build()
    }

    @Provides @Singleton internal fun provideOkHttpClient(interceptors: Set<@JvmSuppressWildcards Interceptor>?): OkHttpClient {
        var okHttpBuilder = OkHttpClient.Builder()

        if (interceptors != null && interceptors.isNotEmpty()) {
            for (interceptor in interceptors) {
                okHttpBuilder.addInterceptor(interceptor)
            }
        }

        okHttpBuilder = okHttpBuilder
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)

        return okHttpBuilder.build()
    }
}
