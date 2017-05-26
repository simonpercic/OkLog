package com.github.simonpercic.oklogexample.ui.base.scope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PerActivity {
}
