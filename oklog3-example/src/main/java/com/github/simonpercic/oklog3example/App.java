package com.github.simonpercic.oklog3example;

import android.app.Application;

import timber.log.Timber;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public class App extends Application {

    @Override public void onCreate() {
        super.onCreate();

        for (Timber.Tree tree : BuildConstants.getTrees()) {
            Timber.plant(tree);
        }
    }
}
