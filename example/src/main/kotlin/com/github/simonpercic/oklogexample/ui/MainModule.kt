package com.github.simonpercic.oklogexample.ui

import com.github.simonpercic.oklogexample.data.api.ApiService
import com.github.simonpercic.oklogexample.ui.base.scope.PerActivity

import dagger.Module
import dagger.Provides

/**
 * MainActivity's Dagger Module.

 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
@Module internal class MainModule(private val view: MainMvp.View) {

    @Provides @PerActivity fun provideView(): MainMvp.View {
        return view
    }

    @Provides @PerActivity fun providePresenter(view: MainMvp.View, apiService: ApiService): MainMvp.Presenter {
        return MainPresenter(view, apiService)
    }
}
