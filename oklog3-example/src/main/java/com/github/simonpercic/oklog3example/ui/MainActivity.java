package com.github.simonpercic.oklog3example.ui;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.simonpercic.oklog3example.R;
import com.github.simonpercic.oklog3example.data.api.ApiService;
import com.github.simonpercic.oklog3example.data.api.ApiServiceProvider;
import com.github.simonpercic.oklog3example.data.api.model.request.show.CreateShowRequest;
import com.github.simonpercic.oklog3example.data.api.model.request.show.ShowNetworkRequest;
import com.github.simonpercic.oklog3example.data.api.model.request.watched.WatchedRequest;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public class MainActivity extends AppCompatActivity {

    private static final ButterKnife.Setter<View, Boolean> ENABLE_VIEW = (view, value, index) -> view.setEnabled(value);

    @Bind(R.id.activity_root_coordinator_layout) CoordinatorLayout coordinatorLayout;
    @Bind({R.id.btn_get, R.id.btn_post, R.id.btn_put, R.id.btn_delete, R.id.btn_header}) List<View> buttonViews;

    private ApiService apiService;
    private Subscription subscription;

    private final Action1<Throwable> errorAction = throwable -> {
        enableButtons();

        String message = String.format("Error: %s", throwable.getMessage());
        showSnackbar(message);
    };

    // region Activity lifecycle callbacks

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiService = ApiServiceProvider.createApiService();

        ButterKnife.bind(this);
    }

    @Override protected void onDestroy() {
        super.onDestroy();

        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    // endregion Activity lifecycle callbacks

    // region Click listeners

    @OnClick(R.id.btn_get)
    public void onBtnGetClicked() {
        disableButtons();

        subscription = apiService.getShows()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResponse -> {
                    enableButtons();
                    showSnackbar(String.format("Got %s TV show items", listResponse.size()));
                }, errorAction);
    }

    @OnClick(R.id.btn_post)
    public void onBtnPostClicked() {
        WatchedRequest request = new WatchedRequest(5);

        disableButtons();

        subscription = apiService.watched(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    enableButtons();
                    showSnackbar(String.format("Watched %s times", response.getWatchedCount()));
                }, errorAction);
    }

    @OnClick(R.id.btn_put)
    public void onBtnPutClicked() {
        ShowNetworkRequest showNetworkRequest = new ShowNetworkRequest(8);
        CreateShowRequest request = new CreateShowRequest("True Detective", 60, showNetworkRequest);

        disableButtons();

        subscription = apiService.createShow(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    enableButtons();
                    showSnackbar(String.format("Created show %s", response.getName()));
                }, errorAction);
    }

    @OnClick(R.id.btn_delete)
    public void onBtnDeleteClicked() {
        disableButtons();

        subscription = apiService.deleteShow(5)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    enableButtons();
                    showSnackbar(String.format("Deleted show %s", response.getName()));
                }, errorAction);
    }

    @OnClick(R.id.btn_header)
    public void onBtnHeaderClicked() {
        disableButtons();

        subscription = apiService.getShowsHeader()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(v -> {
                    enableButtons();
                    showSnackbar("Got header");
                }, errorAction);
    }

    // endregion Click listeners

    // region Helper methods

    private void disableButtons() {
        ButterKnife.apply(buttonViews, ENABLE_VIEW, false);
    }

    private void enableButtons() {
        ButterKnife.apply(buttonViews, ENABLE_VIEW, true);
    }

    private void showSnackbar(String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    // endregion Helper methods
}
