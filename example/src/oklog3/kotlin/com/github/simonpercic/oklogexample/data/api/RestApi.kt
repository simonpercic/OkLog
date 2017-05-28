package com.github.simonpercic.oklogexample.data.api

import com.github.simonpercic.oklogexample.data.api.model.request.show.CreateShowRequest
import com.github.simonpercic.oklogexample.data.api.model.request.watched.WatchedRequest
import com.github.simonpercic.oklogexample.data.api.model.response.show.ShowResponse
import com.github.simonpercic.oklogexample.data.api.model.response.watched.WatchedShowResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HEAD
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import rx.Completable
import rx.Single

/**
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
interface RestApi {

    @GET("shows") fun getShows(): Single<List<ShowResponse>>

    @POST("watched") fun watched(@Body request: WatchedRequest): Single<WatchedShowResponse>

    @PUT("show") fun createShow(@Body request: CreateShowRequest): Single<ShowResponse>

    @DELETE("show/{show}") fun deleteShow(@Path("show") showId: Long): Single<ShowResponse>

    @HEAD("shows") fun getShowsHeader(): Completable
}
