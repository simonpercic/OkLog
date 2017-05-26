package com.github.simonpercic.oklogexample.ui;

/**
 * Main MVP.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
interface MainMvp {

    interface View {

        void enableButtons();

        void disableButtons();

        void showMessage(String message);
    }

    interface Presenter {

        void onBtnGetClicked();

        void onBtnPostClicked();

        void onBtnPutClicked();

        void onBtnDeleteClicked();

        void onBtnHeaderClicked();

        void onDestroy();
    }
}
