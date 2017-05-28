package com.github.simonpercic.oklogexample.data.api

import com.github.simonpercic.oklogexample.data.api.model.request.show.CreateShowRequest
import com.github.simonpercic.oklogexample.data.api.model.request.watched.WatchedRequest
import com.github.simonpercic.oklogexample.data.api.model.response.show.ShowResponse
import com.github.simonpercic.oklogexample.data.api.model.response.watched.WatchedShowResponse

import rx.Completable
import rx.Single

/**
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
interface ApiService {

    fun getShows(): Single<List<ShowResponse>>

    fun watched(request: WatchedRequest): Single<WatchedShowResponse>

    fun createShow(request: CreateShowRequest): Single<ShowResponse>

    fun deleteShow(showId: Long): Single<ShowResponse>

    fun getShowsHeader(): Completable
}
