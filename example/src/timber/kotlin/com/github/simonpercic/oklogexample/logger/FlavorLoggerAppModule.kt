package com.github.simonpercic.oklogexample.logger

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@Module
internal class FlavorLoggerAppModule {

    @Provides @Singleton fun provideTimberInitializer(): LoggerInitializer = TimberInitializer()
}
