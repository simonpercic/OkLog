package com.github.simonpercic.oklogexample

import com.github.simonpercic.oklogexample.data.api.ApiService
import dagger.Component
import javax.inject.Singleton

/**
 * App's Dagger Component.

 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
@Singleton
@Component(modules = arrayOf(AppModule::class, FlavorAppModule::class))
interface AppComponent {

    fun inject(app: App)

    fun apiService(): ApiService
}
