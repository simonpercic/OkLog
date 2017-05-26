package com.github.simonpercic.oklogexample.data.api;

import com.github.simonpercic.oklogexample.data.api.model.request.show.CreateShowRequest;
import com.github.simonpercic.oklogexample.data.api.model.request.watched.WatchedRequest;
import com.github.simonpercic.oklogexample.data.api.model.response.show.ShowResponse;
import com.github.simonpercic.oklogexample.data.api.model.response.watched.WatchedShowResponse;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Completable;
import rx.Single;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public interface RestApi {

    @GET("shows") Single<List<ShowResponse>> getShows();

    @POST("watched") Single<WatchedShowResponse> watched(@Body WatchedRequest request);

    @PUT("show") Single<ShowResponse> createShow(@Body CreateShowRequest request);

    @DELETE("show/{show}") Single<ShowResponse> deleteShow(@Path("show") long showId);

    @HEAD("shows") Completable getShowsHeader();
}
