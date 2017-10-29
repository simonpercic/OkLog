package com.github.simonpercic.oklogexamplekotlin.data.api.okhttp

import com.github.simonpercic.oklog.OkLogInterceptor
import com.squareup.okhttp.Interceptor
import com.squareup.okhttp.OkHttpClient

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import retrofit.Endpoints
import retrofit.RestAdapter
import retrofit.client.OkClient

/**
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
@Module
class DataModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(interceptors: Set<@JvmSuppressWildcards Interceptor>?): OkHttpClient {
        val okHttpClient = OkHttpClient()

        if (interceptors != null && interceptors.isNotEmpty()) {
            okHttpClient.interceptors().addAll(interceptors)
        }

        return okHttpClient
    }

    @Provides
    @Singleton
    fun provideRestApi(okHttpClient: OkHttpClient): RestApi {
        return RestAdapter.Builder()
            .setClient(OkClient(okHttpClient))
            .setEndpoint(Endpoints.newFixedEndpoint(ENDPOINT))
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .build()
            .create(RestApi::class.java)
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
