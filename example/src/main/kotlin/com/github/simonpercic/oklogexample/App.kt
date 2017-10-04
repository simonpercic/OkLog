package com.github.simonpercic.oklogexample

import android.app.Application
import android.content.Context
import com.github.simonpercic.oklogexample.logger.LoggerInitializer

import javax.inject.Inject

/**
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
class App : Application() {

    companion object {

        fun get(context: Context): App {
            return context.applicationContext as App
        }
    }

    @Inject lateinit var loggerInitializer: LoggerInitializer

    lateinit var component: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()

        buildComponentAndInject()

        loggerInitializer.init()
    }

    private fun buildComponentAndInject() {
        component = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()

        component.inject(this)
    }
}
