package com.github.simonpercic.oklogexample

import com.github.simonpercic.oklogexample.data.FlavorDataModule

import dagger.Module

/**
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
@Module(includes = arrayOf(FlavorDataModule::class)) internal class FlavorAppModule
