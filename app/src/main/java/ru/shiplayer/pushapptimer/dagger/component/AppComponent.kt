package ru.shiplayer.pushapptimer.dagger.component

import dagger.Component
import ru.shiplayer.pushapptimer.dagger.models.AppModule
import ru.shiplayer.pushapptimer.dagger.models.TimerModule
import ru.shiplayer.pushapptimer.interactor.TimerStateInteractorImpl
import ru.shiplayer.pushapptimer.service.TimerService
import ru.shiplayer.pushapptimer.ui.viewmodel.TimerViewModel
import javax.inject.Singleton

@Component(modules = [AppModule::class, TimerModule::class])
@Singleton
interface AppComponent{
    fun inject(timerViewModel: TimerViewModel)
    fun inject(interactor: TimerStateInteractorImpl)
    fun inject(service: TimerService)
}