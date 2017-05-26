package com.github.simonpercic.oklogexample;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@Module class AppModule {

    private final App app;

    AppModule(App app) {
        this.app = app;
    }

    @Provides @Singleton App provideApp() {
        return app;
    }

    @Provides @Singleton Context provideContext(App app) {
        return app;
    }
}
