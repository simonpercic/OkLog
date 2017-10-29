package com.github.simonpercic.oklogexamplejava;

import com.github.simonpercic.oklogexamplejava.data.api.okhttp3.DataModule3;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@Singleton
@Component(modules = DataModule3.class)
public interface DataFetcher3Component {

    void inject(DataFetcher3 dataFetcher3);
}
