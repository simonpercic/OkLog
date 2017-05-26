package com.github.simonpercic.oklogexample.ui;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.github.simonpercic.oklogexample.R;
import com.github.simonpercic.oklogexample.ui.base.DaggerActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public class MainActivity extends DaggerActivity<MainComponent> implements MainMvp.View {

    private static final ButterKnife.Setter<View, Boolean> ENABLE_VIEW = (view, value, index) -> view.setEnabled(value);

    @BindView(R.id.activity_root_coordinator_layout) CoordinatorLayout coordinatorLayout;
    @BindViews({R.id.btn_get, R.id.btn_post, R.id.btn_put, R.id.btn_delete, R.id.btn_header}) List<View> buttonViews;

    @Inject MainMvp.Presenter presenter;

    // region Activity lifecycle callbacks

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    // endregion Activity lifecycle callbacks

    // region Abstract impl

    @Override protected MainComponent inject() {
        MainComponent component = DaggerMainComponent.builder()
                .appComponent(getAppComponent())
                .mainModule(new MainModule(this))
                .build();

        component.inject(this);

        return component;
    }

    // endregion Abstract impl

    // region Click listeners

    @OnClick(R.id.btn_get)
    public void onBtnGetClicked() {
        presenter.onBtnGetClicked();
    }

    @OnClick(R.id.btn_post)
    public void onBtnPostClicked() {
        presenter.onBtnPostClicked();
    }

    @OnClick(R.id.btn_put)
    public void onBtnPutClicked() {
        presenter.onBtnPutClicked();
    }

    @OnClick(R.id.btn_delete)
    public void onBtnDeleteClicked() {
        presenter.onBtnDeleteClicked();
    }

    @OnClick(R.id.btn_header)
    public void onBtnHeaderClicked() {
        presenter.onBtnHeaderClicked();
    }

    // endregion Click listeners

    // region Helper methods

    @Override
    public void disableButtons() {
        ButterKnife.apply(buttonViews, ENABLE_VIEW, false);
    }

    @Override
    public void enableButtons() {
        ButterKnife.apply(buttonViews, ENABLE_VIEW, true);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    // endregion Helper methods
}
