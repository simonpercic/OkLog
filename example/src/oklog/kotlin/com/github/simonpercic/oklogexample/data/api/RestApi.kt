package com.github.simonpercic.oklogexample.data.api

import com.github.simonpercic.oklogexample.data.api.model.request.show.CreateShowRequest
import com.github.simonpercic.oklogexample.data.api.model.request.watched.WatchedRequest
import com.github.simonpercic.oklogexample.data.api.model.response.show.ShowResponse
import com.github.simonpercic.oklogexample.data.api.model.response.watched.WatchedShowResponse
import retrofit.http.*
import rx.Observable

/**
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
interface RestApi {

    @GET("/shows") fun getShows(): Observable<List<ShowResponse>>

    @POST("/watched") fun watched(@Body request: WatchedRequest): Observable<WatchedShowResponse>

    @PUT("/show") fun createShow(@Body request: CreateShowRequest): Observable<ShowResponse>

    @DELETE("/show/{show}") fun deleteShow(@Path("show") showId: Long): Observable<ShowResponse>

    @HEAD("/shows") fun getShowsHeader(): Observable<Void>
}
