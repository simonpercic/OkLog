package com.github.simonpercic.oklogexample.data.api.model.response.watched

import com.github.simonpercic.oklogexample.data.api.model.response.show.ShowResponse
import com.google.gson.annotations.SerializedName

/**
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
class WatchedShowResponse {

    val show: ShowResponse? = null

    @SerializedName("watched_count")
    val watchedCount: Int = 0
}
