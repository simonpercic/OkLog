package com.github.simonpercic.oklogexample.ui.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import com.github.simonpercic.oklogexample.App
import com.github.simonpercic.oklogexample.AppComponent

/**
 * Dagger base Activity.

 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
abstract class DaggerActivity<T : Any> : AppCompatActivity() {

    protected lateinit var component: T
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        component = inject()
    }

    /**
     * Inject Dagger components.

     * @return injected Dagger component.
     */
    protected abstract fun inject(): T

    protected val appComponent: AppComponent
        get() = App.get(this).component
}
