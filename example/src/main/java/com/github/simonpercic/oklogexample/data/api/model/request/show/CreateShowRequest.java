package com.github.simonpercic.oklogexample.data.api.model.request.show;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 * @noinspection FieldCanBeLocal
 */
public class CreateShowRequest {

    private final String name;

    private final int runtime;

    private final ShowNetworkRequest network;

    public CreateShowRequest(String name, int runtime, ShowNetworkRequest network) {
        this.name = name;
        this.runtime = runtime;
        this.network = network;
    }
}
