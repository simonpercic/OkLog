package com.github.simonpercic.oklogexample.data.api.model.request.show

/**
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
data class CreateShowRequest(private val name: String, private val runtime: Int, private val network: ShowNetworkRequest)
