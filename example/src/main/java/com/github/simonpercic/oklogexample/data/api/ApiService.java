package com.github.simonpercic.oklogexample.data.api;

import com.github.simonpercic.oklogexample.data.api.model.request.show.CreateShowRequest;
import com.github.simonpercic.oklogexample.data.api.model.request.watched.WatchedRequest;
import com.github.simonpercic.oklogexample.data.api.model.response.show.ShowResponse;
import com.github.simonpercic.oklogexample.data.api.model.response.watched.WatchedShowResponse;

import java.util.List;

import rx.Completable;
import rx.Single;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public interface ApiService {

    Single<List<ShowResponse>> getShows();

    Single<WatchedShowResponse> watched(WatchedRequest request);

    Single<ShowResponse> createShow(CreateShowRequest request);

    Single<ShowResponse> deleteShow(long showId);

    Completable getShowsHeader();
}
