package com.github.simonpercic.oklogexample.ui;

import com.github.simonpercic.oklogexample.data.api.ApiService;
import com.github.simonpercic.oklogexample.ui.base.scope.PerActivity;

import dagger.Module;
import dagger.Provides;

/**
 * MainActivity's Dagger Module.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@Module class MainModule {

    private final MainMvp.View view;

    MainModule(MainMvp.View view) {
        this.view = view;
    }

    @Provides @PerActivity MainMvp.View provideView() {
        return view;
    }

    @Provides @PerActivity MainMvp.Presenter providePresenter(MainMvp.View view, ApiService apiService) {
        return new MainPresenter(view, apiService);
    }
}
