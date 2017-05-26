package com.github.simonpercic.oklogexample.data.api

import com.github.simonpercic.oklogexample.data.api.model.request.show.CreateShowRequest
import com.github.simonpercic.oklogexample.data.api.model.request.watched.WatchedRequest
import com.github.simonpercic.oklogexample.data.api.model.response.show.ShowResponse
import com.github.simonpercic.oklogexample.data.api.model.response.watched.WatchedShowResponse
import retrofit2.http.*
import rx.Completable
import rx.Single

/**
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
interface RestApi {

    @get:GET("shows") val shows: Single<List<ShowResponse>>

    @POST("watched") fun watched(@Body request: WatchedRequest): Single<WatchedShowResponse>

    @PUT("show") fun createShow(@Body request: CreateShowRequest): Single<ShowResponse>

    @DELETE("show/{show}") fun deleteShow(@Path("show") showId: Long): Single<ShowResponse>

    @get:HEAD("shows") val showsHeader: Completable
}
