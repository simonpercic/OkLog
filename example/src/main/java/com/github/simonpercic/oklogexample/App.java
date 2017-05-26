package com.github.simonpercic.oklogexample;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import timber.log.Timber;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public class App extends Application {

    private AppComponent component;

    @Override public void onCreate() {
        super.onCreate();

        buildComponentAndInject();

        // remember to never log in production in your apps!
        Timber.plant(new Timber.DebugTree());
    }

    private void buildComponentAndInject() {
        component = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

        component.inject(this);
    }

    @NonNull public AppComponent getComponent() {
        return component;
    }

    public static App get(@NonNull Context context) {
        return (App) context.getApplicationContext();
    }
}
