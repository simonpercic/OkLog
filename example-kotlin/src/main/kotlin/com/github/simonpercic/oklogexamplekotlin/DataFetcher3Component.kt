package com.github.simonpercic.oklogexamplekotlin

import com.github.simonpercic.oklogexamplekotlin.data.api.okhttp3.DataModule3

import javax.inject.Singleton

import dagger.Component

/**
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
@Singleton
@Component(modules = arrayOf(DataModule3::class))
interface DataFetcher3Component {

    fun inject(dataFetcher3: DataFetcher3)
}
