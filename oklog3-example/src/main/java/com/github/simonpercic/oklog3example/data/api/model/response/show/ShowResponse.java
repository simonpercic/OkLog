package com.github.simonpercic.oklog3example.data.api.model.response.show;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public class ShowResponse {

    private long id;

    private String name;

    private int runtime;

    private ShowNetworkResponse network;

    // region getters

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getRuntime() {
        return runtime;
    }

    public ShowNetworkResponse getNetwork() {
        return network;
    }

    // endregion getters
}
