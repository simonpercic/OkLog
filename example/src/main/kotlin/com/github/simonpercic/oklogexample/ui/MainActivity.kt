package com.github.simonpercic.oklogexample.ui

import android.os.Bundle
import android.support.design.widget.Snackbar
import com.github.simonpercic.oklogexample.R
import com.github.simonpercic.oklogexample.ui.base.DaggerActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

/**
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
class MainActivity : DaggerActivity<MainComponent>(), MainMvp.View {

    @Inject internal lateinit var presenter: MainMvp.Presenter

    // region Activity lifecycle callbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnGet.setOnClickListener { presenter.onBtnGetClicked() }
        btnPost.setOnClickListener { presenter.onBtnPostClicked() }
        btnPut.setOnClickListener { presenter.onBtnPutClicked() }
        btnDelete.setOnClickListener { presenter.onBtnDeleteClicked() }
        btnHeader.setOnClickListener { presenter.onBtnHeaderClicked() }
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

    // region View impl

    override fun disableButtons() {
        setButtonsEnabled(false)
    }

    override fun enableButtons() {
        setButtonsEnabled(true)
    }

    override fun showMessage(message: String) {
        Snackbar.make(rootCoordinatorLayout, message, Snackbar.LENGTH_SHORT).show()
    }

    // endregion View impl

    // region Helper methods

    private fun setButtonsEnabled(enabled: Boolean) {
        btnGet.isEnabled = enabled
        btnPost.isEnabled = enabled
        btnPut.isEnabled = enabled
        btnDelete.isEnabled = enabled
        btnHeader.isEnabled = enabled
    }

    // endregion Helper methods
}
