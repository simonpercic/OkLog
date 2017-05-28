package com.github.simonpercic.oklogexample.ui

/**
 * Main MVP.

 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
internal interface MainMvp {

    interface View {

        fun enableButtons()

        fun disableButtons()

        fun showMessage(message: String)
    }

    interface Presenter {

        fun onBtnGetClicked()

        fun onBtnPostClicked()

        fun onBtnPutClicked()

        fun onBtnDeleteClicked()

        fun onBtnHeaderClicked()

        fun onDestroy()
    }
}
