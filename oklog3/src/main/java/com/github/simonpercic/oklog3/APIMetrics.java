package com.github.simonpercic.oklog3;

/**
 * @hannanshaik <a href="https://github.com/hannanshaik">https://github.com/hannanshaik</a>
 */

/**
 * To capture any kind of metrics during or after the API call.
 */
public abstract class APIMetrics {
    public abstract void onCaptureResponseTime(long timeTaken);
}
