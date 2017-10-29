package com.github.simonpercic.oklogexamplekotlin.data.api.okhttp

import com.github.simonpercic.oklogexamplekotlin.data.api.model.response.show.ShowResponse

import retrofit.http.GET

/**
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
interface RestApi {

    @GET("/shows")
    fun getShows(): List<ShowResponse>
}
