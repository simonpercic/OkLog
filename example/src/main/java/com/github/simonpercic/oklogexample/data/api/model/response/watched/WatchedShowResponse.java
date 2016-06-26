package com.github.simonpercic.oklogexample.data.api.model.response.watched;

import com.github.simonpercic.oklogexample.data.api.model.response.show.ShowResponse;
import com.google.gson.annotations.SerializedName;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public class WatchedShowResponse {

    private ShowResponse show;

    @SerializedName("watched_count")
    private int watchedCount;

    // region getters

    public ShowResponse getShow() {
        return show;
    }

    public int getWatchedCount() {
        return watchedCount;
    }

    // endregion getters
}
