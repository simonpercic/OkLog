package com.github.simonpercic.oklogexample.logger

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
class TimberInitializer : LoggerInitializer {

    override fun init() {

        // remember to never log in production in your apps!
        Timber.plant(Timber.DebugTree())
    }
}
