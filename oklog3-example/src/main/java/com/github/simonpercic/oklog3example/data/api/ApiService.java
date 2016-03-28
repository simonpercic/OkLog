package com.github.simonpercic.oklog3example.data.api;

import com.github.simonpercic.oklog3example.data.api.model.request.show.CreateShowRequest;
import com.github.simonpercic.oklog3example.data.api.model.request.watched.WatchedRequest;
import com.github.simonpercic.oklog3example.data.api.model.response.show.ShowResponse;
import com.github.simonpercic.oklog3example.data.api.model.response.watched.WatchedShowResponse;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public interface ApiService {

    @GET("shows") Observable<List<ShowResponse>> getShows();

    @POST("watched") Observable<WatchedShowResponse> watched(@Body WatchedRequest request);

    @PUT("show") Observable<ShowResponse> createShow(@Body CreateShowRequest request);

    @DELETE("show/{show}") Observable<ShowResponse> deleteShow(@Path("show") long showId);

    @HEAD("shows") Observable<Void> getShowsHeader();
}
