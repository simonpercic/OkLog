package com.github.simonpercic.oklogexamplekotlin

import com.github.simonpercic.oklogexamplekotlin.data.api.okhttp.DataModule

import javax.inject.Singleton

import dagger.Component

/**
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
@Singleton
@Component(modules = arrayOf(DataModule::class))
interface DataFetcherComponent {

    fun inject(dataFetcher: DataFetcher)
}
