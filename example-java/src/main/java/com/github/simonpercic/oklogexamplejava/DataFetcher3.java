package com.github.simonpercic.oklogexamplejava;

import com.github.simonpercic.oklogexamplejava.data.api.okhttp3.DataModule3;
import com.github.simonpercic.oklogexamplejava.data.api.okhttp3.RestApi3;
import com.github.simonpercic.oklogexamplejava.data.api.model.response.show.ShowResponse;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Response;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
class DataFetcher3 {

    @Inject RestApi3 restApi;

    void fetch() {
        DaggerDataFetcher3Component.builder()
                .dataModule3(new DataModule3())
                .build()
                .inject(this);

        try {
            Response<List<ShowResponse>> response = restApi.getShows().execute();

            if (response.isSuccessful()) {
                System.out.printf("Got %d shows%n", response.body().size());
            } else {
               System.out.println("Call failed");
            }
        } catch (IOException e) {
            System.out.printf("Call failed with %s%n", e.getMessage());
        }
    }
}
