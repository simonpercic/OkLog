package com.github.simonpercic.oklogexamplekotlin

import com.github.simonpercic.oklogexamplekotlin.data.api.okhttp.DataModule
import com.github.simonpercic.oklogexamplekotlin.data.api.okhttp.RestApi
import javax.inject.Inject

/**
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
class DataFetcher {

    @Inject
    lateinit var restApi: RestApi

    fun fetch() {
        DaggerDataFetcherComponent.builder()
            .dataModule(DataModule())
            .build()
            .inject(this)

        val response = restApi.getShows()

        System.out.printf("Got %d shows%n", response.size)
    }
}
