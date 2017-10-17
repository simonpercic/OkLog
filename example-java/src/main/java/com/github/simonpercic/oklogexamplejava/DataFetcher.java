package com.github.simonpercic.oklogexamplejava;

import com.github.simonpercic.oklogexamplejava.data.api.model.response.show.ShowResponse;
import com.github.simonpercic.oklogexamplejava.data.api.okhttp.DataModule;
import com.github.simonpercic.oklogexamplejava.data.api.okhttp.RestApi;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Response;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
class DataFetcher {

    @Inject RestApi restApi;

    void fetch() {
        DaggerDataFetcherComponent.builder()
                .dataModule(new DataModule())
                .build()
                .inject(this);

        List<ShowResponse> response = restApi.getShows();

        System.out.printf("Got %d shows%n", response.size());
    }
}
