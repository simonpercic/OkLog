package com.github.simonpercic.oklogexample.ui.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.simonpercic.oklogexample.App;
import com.github.simonpercic.oklogexample.AppComponent;

/**
 * Dagger base Activity.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public abstract class DaggerActivity<T> extends AppCompatActivity {

    private T component;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        component = inject();
    }

    /**
     * Inject Dagger components.
     *
     * @return injected Dagger component.
     */
    protected abstract T inject();

    public T getComponent() {
        return component;
    }

    protected AppComponent getAppComponent() {
        return App.get(this).getComponent();
    }
}
