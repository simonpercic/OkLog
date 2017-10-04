package com.github.simonpercic.oklogexample

import android.content.Context
import com.github.simonpercic.oklogexample.logger.FlavorLoggerAppModule
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
@Module(includes = arrayOf(FlavorLoggerAppModule::class))
internal class AppModule(private val app: App) {

    @Provides @Singleton fun provideApp(): App {
        return app
    }

    @Provides @Singleton fun provideContext(app: App): Context {
        return app
    }
}
