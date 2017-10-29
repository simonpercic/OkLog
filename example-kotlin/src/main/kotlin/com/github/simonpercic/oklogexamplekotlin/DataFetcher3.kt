package com.github.simonpercic.oklogexamplekotlin

import com.github.simonpercic.oklogexamplekotlin.data.api.okhttp3.DataModule3
import com.github.simonpercic.oklogexamplekotlin.data.api.okhttp3.RestApi3
import java.io.IOException
import javax.inject.Inject

/**
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
class DataFetcher3 {

    @Inject
    lateinit var restApi: RestApi3

    fun fetch() {
        DaggerDataFetcher3Component.builder()
            .dataModule3(DataModule3())
            .build()
            .inject(this)

        try {
            val response = restApi.getShows().execute()

            if (response.isSuccessful) {
                System.out.printf("Got %d shows%n", response.body()!!.size)
            } else {
                println("Call failed")
            }
        } catch (e: IOException) {
            System.out.printf("Call failed with %s%n", e.message)
        }

    }
}
