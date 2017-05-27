package com.github.simonpercic.oklogexample.data.api

import com.github.simonpercic.oklogexample.data.api.model.request.show.CreateShowRequest
import com.github.simonpercic.oklogexample.data.api.model.request.watched.WatchedRequest
import com.github.simonpercic.oklogexample.data.api.model.response.show.ShowResponse
import com.github.simonpercic.oklogexample.data.api.model.response.watched.WatchedShowResponse

import javax.inject.Inject
import javax.inject.Singleton

import rx.Completable
import rx.Single

/**
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
@Singleton
class RestApiService @Inject internal constructor(private val restApi: RestApi) : ApiService {

    override fun getShows(): Single<List<ShowResponse>> {
        return restApi.getShows().toSingle()
    }

    override fun watched(request: WatchedRequest): Single<WatchedShowResponse> {
        return restApi.watched(request).toSingle()
    }

    override fun createShow(request: CreateShowRequest): Single<ShowResponse> {
        return restApi.createShow(request).toSingle()
    }

    override fun deleteShow(showId: Long): Single<ShowResponse> {
        return restApi.deleteShow(showId).toSingle()
    }

    override fun getShowsHeader(): Completable {
        return restApi.getShowsHeader().toCompletable()
    }
}
