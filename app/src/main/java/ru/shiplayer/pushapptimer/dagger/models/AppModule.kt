package ru.shiplayer.pushapptimer.dagger.models

import android.content.Context
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides

@Module
class AppModule(val appContext: Context) {

    @Provides
    fun provideContext() = appContext

    @Provides
    fun getSharedPreference() = PreferenceManager.getDefaultSharedPreferences(appContext)
}