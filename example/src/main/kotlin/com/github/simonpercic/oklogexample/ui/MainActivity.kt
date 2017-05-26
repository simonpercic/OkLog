package com.github.simonpercic.oklogexample.ui

import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.view.View
import butterknife.BindView
import butterknife.BindViews
import butterknife.ButterKnife
import butterknife.OnClick
import com.github.simonpercic.oklogexample.R
import com.github.simonpercic.oklogexample.ui.base.DaggerActivity
import javax.inject.Inject

/**
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
class MainActivity : DaggerActivity<MainComponent>(), MainMvp.View {

    companion object {

        private val ENABLE_VIEW: ButterKnife.Setter<View, Boolean> = ButterKnife.Setter { view, value, _ -> view.isEnabled = value }
    }

    @BindView(R.id.activity_root_coordinator_layout) internal lateinit var coordinatorLayout: CoordinatorLayout
    @BindViews(R.id.btn_get, R.id.btn_post, R.id.btn_put, R.id.btn_delete, R.id.btn_header) internal lateinit var buttonViews: List<@JvmSuppressWildcards View>

    @Inject internal lateinit var presenter: MainMvp.Presenter

    // region Activity lifecycle callbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    // endregion Activity lifecycle callbacks

    // region Abstract impl

    override fun inject(): MainComponent {
        val component = DaggerMainComponent.builder()
                .appComponent(appComponent)
                .mainModule(MainModule(this))
                .build()

        component.inject(this)

        return component
    }

    // endregion Abstract impl

    // region Click listeners

    @OnClick(R.id.btn_get)
    fun onBtnGetClicked() {
        presenter.onBtnGetClicked()
    }

    @OnClick(R.id.btn_post)
    fun onBtnPostClicked() {
        presenter.onBtnPostClicked()
    }

    @OnClick(R.id.btn_put)
    fun onBtnPutClicked() {
        presenter.onBtnPutClicked()
    }

    @OnClick(R.id.btn_delete)
    fun onBtnDeleteClicked() {
        presenter.onBtnDeleteClicked()
    }

    @OnClick(R.id.btn_header)
    fun onBtnHeaderClicked() {
        presenter.onBtnHeaderClicked()
    }

    // endregion Click listeners

    // region Helper methods

    override fun disableButtons() {
        ButterKnife.apply(buttonViews, ENABLE_VIEW, false)
    }

    override fun enableButtons() {
        ButterKnife.apply(buttonViews, ENABLE_VIEW, true)
    }

    override fun showMessage(message: String) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show()
    }

    // endregion Helper methods
}
