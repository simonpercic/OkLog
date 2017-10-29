package com.github.simonpercic.oklogexamplekotlin.data.api.okhttp3

import com.github.simonpercic.oklog3.OkLogInterceptor

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
@Module
class DataModule3 {

    @Provides
    @Singleton
    fun provideOkHttpClient(interceptors: Set<@JvmSuppressWildcards Interceptor>?): OkHttpClient {
        val okHttpBuilder = OkHttpClient.Builder()

        if (interceptors != null && interceptors.isNotEmpty()) {
            for (interceptor in interceptors) {
                okHttpBuilder.addInterceptor(interceptor)
            }
        }

        return okHttpBuilder.build()
    }

    @Provides
    @Singleton
    fun provideRestApi(okHttpClient: OkHttpClient): RestApi3 {
        return Retrofit.Builder()
            .baseUrl(ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(RestApi3::class.java)
    }

    @Provides
    @IntoSet
    @Singleton
    fun provideHttpLoggingInterceptor(): Interceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return httpLoggingInterceptor
    }

    @Provides
    @IntoSet
    @Singleton
    fun provideOkLogInterceptor(): Interceptor {
        return OkLogInterceptor.builder().build()
    }

    companion object {

        private val ENDPOINT = "http://private-b7bc4-tvshowsapi.apiary-mock.com"
    }
}
