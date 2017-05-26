package com.github.simonpercic.oklogexample.ui;

import com.github.simonpercic.oklogexample.data.api.ApiService;
import com.github.simonpercic.oklogexample.data.api.model.request.show.CreateShowRequest;
import com.github.simonpercic.oklogexample.data.api.model.request.show.ShowNetworkRequest;
import com.github.simonpercic.oklogexample.data.api.model.request.watched.WatchedRequest;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Main Activity's presenter.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
class MainPresenter implements MainMvp.Presenter {

    private final MainMvp.View view;
    private final ApiService apiService;
    private final Action1<Throwable> errorAction;

    private Subscription subscription;

    MainPresenter(MainMvp.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;

        this.errorAction = throwable -> {
            MainPresenter.this.view.enableButtons();

            String message = String.format("Error: %s", throwable.getMessage());
            MainPresenter.this.view.showMessage(message);
        };
    }

    @Override public void onBtnGetClicked() {
        view.disableButtons();

        subscription = apiService.getShows()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResponse -> {
                    view.enableButtons();
                    view.showMessage(String.format("Got %s TV show items", listResponse.size()));
                }, errorAction);
    }

    @Override public void onBtnPostClicked() {
        WatchedRequest request = new WatchedRequest(5);

        view.disableButtons();

        subscription = apiService.watched(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    view.enableButtons();
                    view.showMessage(String.format("Watched %s times", response.getWatchedCount()));
                }, errorAction);
    }

    @Override public void onBtnPutClicked() {
        ShowNetworkRequest showNetworkRequest = new ShowNetworkRequest(8);
        CreateShowRequest request = new CreateShowRequest("True Detective", 60, showNetworkRequest);

        view.disableButtons();

        subscription = apiService.createShow(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    view.enableButtons();
                    view.showMessage(String.format("Created show %s", response.getName()));
                }, errorAction);
    }

    @Override public void onBtnDeleteClicked() {
        view.disableButtons();

        subscription = apiService.deleteShow(5)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    view.enableButtons();
                    view.showMessage(String.format("Deleted show %s", response.getName()));
                }, errorAction);
    }

    @Override public void onBtnHeaderClicked() {
        view.disableButtons();

        subscription = apiService.getShowsHeader()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    view.enableButtons();
                    view.showMessage("Got header");
                }, errorAction);
    }

    @Override public void onDestroy() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
