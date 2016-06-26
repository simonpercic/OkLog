package com.github.simonpercic.oklogexample;

import timber.log.Timber;
import timber.log.Timber.DebugTree;
import timber.log.Timber.Tree;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public final class BuildConstants {

    private BuildConstants() {
        // no instance
    }

    public static Timber.Tree[] getTrees() {
        return new Tree[]{
                new DebugTree()
        };
    }
}
