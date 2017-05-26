package com.github.simonpercic.oklogexample;

import com.github.simonpercic.oklogexample.data.api.ApiService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * App's Dagger Component.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@Singleton
@Component(
        modules = {
                AppModule.class,
                FlavorAppModule.class
        }
)
public interface AppComponent {

    void inject(App app);

    ApiService apiService();
}
