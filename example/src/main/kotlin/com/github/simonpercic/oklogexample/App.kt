package com.github.simonpercic.oklogexample

import android.app.Application
import android.content.Context

import timber.log.Timber

/**
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
class App : Application() {

    companion object {

        fun get(context: Context): App {
            return context.applicationContext as App
        }
    }

    lateinit var component: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()

        buildComponentAndInject()

        // remember to never log in production in your apps!
        Timber.plant(Timber.DebugTree())
    }

    private fun buildComponentAndInject() {
        component = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()

        component.inject(this)
    }
}
