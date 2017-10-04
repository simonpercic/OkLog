package com.github.simonpercic.oklog.core;

/**
 * Pure-Java logger implementation.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
class JavaLogger implements Logger {

    private final java.util.logging.Logger logger = java.util.logging.Logger.getLogger("com.github.simonpercic.oklog");

    /**
     * {@inheritDoc}
     */
    @Override public void d(String tag, String message) {
        logger.info(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void w(String tag, String message) {
        logger.warning(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void e(String tag, String message, Throwable throwable) {
        logger.severe(message);
    }
}
