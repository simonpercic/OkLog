package com.github.simonpercic.oklogexamplekotlin.data.api.model.response.show

/**
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
class ShowResponse {

    var id: Long = 0
    var name: String? = null
    var runtime: Int = 0
    var network: ShowNetworkResponse? = null
}
