package ru.shiplayer.pushapptimer.interactor

import android.content.SharedPreferences
import android.util.Log
import io.reactivex.Single
import ru.shiplayer.pushapptimer.App
import ru.shiplayer.pushapptimer.model.TimerModel
import java.util.*
import javax.inject.Inject

class TimerStateInteractorImpl() : TimerStateInteractor {

    @Inject
    lateinit var preferences: SharedPreferences

    companion object {
        const val END_TIMER_PREFERENCES_KEY = "end_timer_preferences_key"
        const val IS_STARTED_PREFERENCE_KEY = "IS_STARTED_PREFERENCE_KEY"
    }

    init {
        App.component.inject(this)
    }

    override fun saveInPreference(timerModule: TimerModel) {
        val editable = preferences.edit()
        if (timerModule.endTime != null) {
            editable.putLong(END_TIMER_PREFERENCES_KEY, timerModule.endTime.time)
        }
        editable.putBoolean(IS_STARTED_PREFERENCE_KEY, timerModule.isStarted)
            .apply()
    }

    override fun loadFromPreference(): Single<TimerModel> {
        return Single.just(
            if (preferences.contains(END_TIMER_PREFERENCES_KEY) && preferences.getBoolean(IS_STARTED_PREFERENCE_KEY, false)) {
                val time = preferences.getLong(END_TIMER_PREFERENCES_KEY, -1)
                Log.i("Interactor", Date(time).toString())
                val started = preferences.getBoolean(IS_STARTED_PREFERENCE_KEY, false)
                if (time != -1.toLong() && time - Date().time > 0) {
                    TimerModel.instanceOf(time - Date().time).copy(isStarted = started)
                } else
                    TimerModel()
            } else
                TimerModel()
        ).doOnSuccess {
            Log.i("Interactor", it.toString())
        }
    }

}