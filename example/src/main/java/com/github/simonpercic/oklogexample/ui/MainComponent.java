package com.github.simonpercic.oklogexample.ui;

import com.github.simonpercic.oklogexample.AppComponent;
import com.github.simonpercic.oklogexample.ui.base.scope.PerActivity;

import dagger.Component;

/**
 * Main Activity's Dagger component.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@PerActivity
@Component(
        dependencies = AppComponent.class,
        modules = MainModule.class
) interface MainComponent {

    void inject(MainActivity activity);
}
