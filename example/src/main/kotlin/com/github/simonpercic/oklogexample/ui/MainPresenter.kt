package com.github.simonpercic.oklogexample.ui

import com.github.simonpercic.oklogexample.data.api.ApiService
import com.github.simonpercic.oklogexample.data.api.model.request.show.CreateShowRequest
import com.github.simonpercic.oklogexample.data.api.model.request.show.ShowNetworkRequest
import com.github.simonpercic.oklogexample.data.api.model.request.watched.WatchedRequest
import com.github.simonpercic.oklogexample.data.api.model.response.show.ShowResponse
import com.github.simonpercic.oklogexample.data.api.model.response.watched.WatchedShowResponse

import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action0
import rx.functions.Action1

/**
 * Main Activity's presenter.

 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
internal class MainPresenter(private val view: MainMvp.View, private val apiService: ApiService) : MainMvp.Presenter {

    private val errorAction: Action1<Throwable>

    private var subscription: Subscription? = null

    init {

        this.errorAction = Action1<Throwable> {
            this@MainPresenter.view.enableButtons()

            val message = String.format("Error: %s", it.message)
            this@MainPresenter.view.showMessage(message)
        }
    }

    override fun onBtnGetClicked() {
        view.disableButtons()

        subscription = apiService.getShows()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Action1 {
                    view.enableButtons()
                    view.showMessage(String.format("Got %s TV show items", it.size))
                }, errorAction)
    }

    override fun onBtnPostClicked() {
        val request = WatchedRequest(5)

        view.disableButtons()

        subscription = apiService.watched(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Action1<WatchedShowResponse> {
                    view.enableButtons()
                    view.showMessage(String.format("Watched %s times", it.watchedCount))
                }, errorAction)
    }

    override fun onBtnPutClicked() {
        val showNetworkRequest = ShowNetworkRequest(8)
        val request = CreateShowRequest("True Detective", 60, showNetworkRequest)

        view.disableButtons()

        subscription = apiService.createShow(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Action1<ShowResponse> {
                    view.enableButtons()
                    view.showMessage(String.format("Created show %s", it.name))
                }, errorAction)
    }

    override fun onBtnDeleteClicked() {
        view.disableButtons()

        subscription = apiService.deleteShow(5)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Action1<ShowResponse> {
                    view.enableButtons()
                    view.showMessage(String.format("Deleted show %s", it.name))
                }, errorAction)
    }

    override fun onBtnHeaderClicked() {
        view.disableButtons()

        subscription = apiService.getShowsHeader()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Action0 {
                    view.enableButtons()
                    view.showMessage("Got header")
                }, errorAction)
    }

    override fun onDestroy() {
        if (subscription != null && !subscription!!.isUnsubscribed) {
            subscription!!.unsubscribe()
        }
    }
}
