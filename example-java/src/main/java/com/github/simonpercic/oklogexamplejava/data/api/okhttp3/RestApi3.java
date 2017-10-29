package com.github.simonpercic.oklogexamplejava.data.api.okhttp3;

import com.github.simonpercic.oklogexamplejava.data.api.model.response.show.ShowResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public interface RestApi3 {

    @GET("/shows")
    Call<List<ShowResponse>> getShows();
}
