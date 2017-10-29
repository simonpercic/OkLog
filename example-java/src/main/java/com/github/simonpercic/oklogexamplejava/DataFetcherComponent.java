package com.github.simonpercic.oklogexamplejava;

import com.github.simonpercic.oklogexamplejava.data.api.okhttp.DataModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@Singleton
@Component(modules = DataModule.class)
public interface DataFetcherComponent {

    void inject(DataFetcher dataFetcher);
}
