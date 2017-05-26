package com.github.simonpercic.oklogexample.ui

import com.github.simonpercic.oklogexample.AppComponent
import com.github.simonpercic.oklogexample.ui.base.scope.PerActivity

import dagger.Component

/**
 * Main Activity's Dagger component.

 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
@PerActivity
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(MainModule::class))
interface MainComponent {

    fun inject(activity: MainActivity)
}
