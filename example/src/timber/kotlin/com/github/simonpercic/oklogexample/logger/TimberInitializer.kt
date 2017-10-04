package com.github.simonpercic.oklogexample.logger

import timber.log.Timber

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
class TimberInitializer : LoggerInitializer {

    override fun init() {

        // remember to never log in production in your apps!
        Timber.plant(Timber.DebugTree())
    }
}
