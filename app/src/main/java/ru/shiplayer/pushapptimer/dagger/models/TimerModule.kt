package ru.shiplayer.pushapptimer.dagger.models

import dagger.Module
import dagger.Provides
import ru.shiplayer.pushapptimer.interactor.TimerStateInteractor
import ru.shiplayer.pushapptimer.interactor.TimerStateInteractorImpl

@Module
class TimerModule {

    @Provides
    fun provideTimerStateInteractor(): TimerStateInteractor = TimerStateInteractorImpl()
}