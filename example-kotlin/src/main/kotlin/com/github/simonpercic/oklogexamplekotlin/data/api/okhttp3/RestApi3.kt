package com.github.simonpercic.oklogexamplekotlin.data.api.okhttp3

import com.github.simonpercic.oklogexamplekotlin.data.api.model.response.show.ShowResponse

import retrofit2.Call
import retrofit2.http.GET

/**
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
interface RestApi3 {

    @GET("/shows")
    fun getShows(): Call<List<ShowResponse>>
}
