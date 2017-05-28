package com.github.simonpercic.oklogexample

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
@Module internal class AppModule(private val app: App) {

    @Provides @Singleton fun provideApp(): App {
        return app
    }

    @Provides @Singleton fun provideContext(app: App): Context {
        return app
    }
}
