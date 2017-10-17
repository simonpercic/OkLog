package com.github.simonpercic.oklogexamplejava.data.api.okhttp;

import com.github.simonpercic.oklogexamplejava.data.api.model.response.show.ShowResponse;

import java.util.List;

import retrofit.http.GET;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public interface RestApi {

    @GET("/shows")
    List<ShowResponse> getShows();
}
