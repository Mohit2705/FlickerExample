package com.example.msharma.practice.di.module

import com.example.msharma.practice.LaunchActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector()
    abstract fun launchActivity(): LaunchActivity
}