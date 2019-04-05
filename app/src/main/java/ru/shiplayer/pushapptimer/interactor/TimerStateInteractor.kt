package ru.shiplayer.pushapptimer.interactor

import io.reactivex.Single
import ru.shiplayer.pushapptimer.model.TimerModel

interface TimerStateInteractor{
    fun loadFromPreference(): Single<TimerModel>
    fun saveInPreference(timerModule: TimerModel)
}