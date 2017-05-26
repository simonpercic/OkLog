package com.github.simonpercic.oklogexample.data.api;

import com.github.simonpercic.oklogexample.data.api.model.request.show.CreateShowRequest;
import com.github.simonpercic.oklogexample.data.api.model.request.watched.WatchedRequest;
import com.github.simonpercic.oklogexample.data.api.model.response.show.ShowResponse;
import com.github.simonpercic.oklogexample.data.api.model.response.watched.WatchedShowResponse;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.HEAD;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import rx.Observable;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public interface RestApi {

    @GET("/shows") Observable<List<ShowResponse>> getShows();

    @POST("/watched") Observable<WatchedShowResponse> watched(@Body WatchedRequest request);

    @PUT("/show") Observable<ShowResponse> createShow(@Body CreateShowRequest request);

    @DELETE("/show/{show}") Observable<ShowResponse> deleteShow(@Path("show") long showId);

    @HEAD("/shows") Observable<Void> getShowsHeader();
}
