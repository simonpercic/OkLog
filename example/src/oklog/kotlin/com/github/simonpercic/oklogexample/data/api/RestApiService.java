package com.github.simonpercic.oklogexample.data.api;

import com.github.simonpercic.oklogexample.data.api.model.request.show.CreateShowRequest;
import com.github.simonpercic.oklogexample.data.api.model.request.watched.WatchedRequest;
import com.github.simonpercic.oklogexample.data.api.model.response.show.ShowResponse;
import com.github.simonpercic.oklogexample.data.api.model.response.watched.WatchedShowResponse;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Completable;
import rx.Single;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@Singleton
public class RestApiService implements ApiService {

    private final RestApi restApi;

    @Inject RestApiService(RestApi restApi) {
        this.restApi = restApi;
    }

    @Override
    public Single<List<ShowResponse>> getShows() {
        return restApi.getShows().toSingle();
    }

    @Override
    public Single<WatchedShowResponse> watched(WatchedRequest request) {
        return restApi.watched(request).toSingle();
    }

    @Override
    public Single<ShowResponse> createShow(CreateShowRequest request) {
        return restApi.createShow(request).toSingle();
    }

    @Override
    public Single<ShowResponse> deleteShow(long showId) {
        return restApi.deleteShow(showId).toSingle();
    }

    @Override
    public Completable getShowsHeader() {
        return restApi.getShowsHeader().toCompletable();
    }
}
