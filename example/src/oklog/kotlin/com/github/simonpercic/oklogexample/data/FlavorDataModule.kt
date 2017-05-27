package com.github.simonpercic.oklogexample.data

import com.github.simonpercic.oklog.OkLogInterceptor
import com.github.simonpercic.oklogexample.Constants
import com.github.simonpercic.oklogexample.data.api.ApiService
import com.github.simonpercic.oklogexample.data.api.RestApi
import com.github.simonpercic.oklogexample.data.api.RestApiService
import com.squareup.okhttp.Interceptor
import com.squareup.okhttp.OkHttpClient
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import retrofit.Endpoints
import retrofit.RestAdapter
import retrofit.client.OkClient
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
        return RestAdapter.Builder()
                .setClient(OkClient(okHttpClient))
                .setEndpoint(Endpoints.newFixedEndpoint(Constants.ENDPOINT))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build()
                .create(RestApi::class.java)
    }

    @Provides @IntoSet @Singleton internal fun provideOkLogInterceptor(): Interceptor {
        return OkLogInterceptor.builder().build()
    }

    @Provides @Singleton internal fun provideOkHttpClient(interceptors: Set<@JvmSuppressWildcards Interceptor>?): OkHttpClient {
        val client = OkHttpClient()

        val clientInterceptors = client.interceptors()

        if (interceptors != null && interceptors.isNotEmpty()) {
            for (interceptor in interceptors) {
                clientInterceptors.add(interceptor)
            }
        }

        return client
    }
}
