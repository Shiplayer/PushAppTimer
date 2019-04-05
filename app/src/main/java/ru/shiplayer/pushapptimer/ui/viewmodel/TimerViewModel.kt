package ru.shiplayer.pushapptimer.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import ru.shiplayer.pushapptimer.App
import ru.shiplayer.pushapptimer.interactor.TimerStateInteractor
import ru.shiplayer.pushapptimer.model.TimerModel
import ru.shiplayer.pushapptimer.service.TimerService
import ru.shiplayer.pushapptimer.ui.MainActivity
import javax.inject.Inject

class TimerViewModel : ViewModel() {
    @Inject
    lateinit var timerStateInteractor: TimerStateInteractor

    val modelLiveData = MutableLiveData<TimerModel>()
    val finishedTimer = MutableLiveData<Boolean>()
    var currentModel = TimerModel()
    var disposable: Disposable? = null
    private val inputNumberSubject = PublishSubject.create<Int>()
    private var oldDigits = 30
    private var timeCountDown: CountDownTimer? = null
    init {
        modelLiveData.value = currentModel
        App.component.inject(this)
    }

    fun action(digit: Int) {
        oldDigits = (oldDigits * 10 + digit) % 1000000
        currentModel = currentModel.updateTime(oldDigits)
        modelLiveData.value = currentModel
    }

/*    fun selectedView(id: Int) {
        currentModel = when(id) {
            R.id.tv_timer_hours -> currentModel.copy(selectedTime = SelectedTime.HOURS)
            R.id.tv_timer_minutes -> currentModel.copy(selectedTime = SelectedTime.MINUTES)
            R.id.tv_timer_seconds -> currentModel.copy(selectedTime = SelectedTime.SECONDS)
            else -> currentModel
        }
        modelLiveData.postValue(currentModel)
    }*/

    fun saveInstance(){
        timerStateInteractor.saveInPreference(currentModel)
    }

    fun deleteLastDigit() {
        oldDigits /= 10
        currentModel = currentModel.updateTime(oldDigits)
        modelLiveData.value = currentModel
    }

    fun clear() {
        currentModel = TimerModel()
        modelLiveData.value = currentModel
        oldDigits = 30
    }

    fun startTimer(context: Context) {
        currentModel = currentModel.calculateTime()
        modelLiveData.value = currentModel
        start()
        val intent = Intent(context, TimerService::class.java)
        intent.putExtra(MainActivity.TIMER_MODEL_KEY, currentModel)
        intent.putExtra("running", true)
        ContextCompat.startForegroundService(context, intent)
    }

    private fun start() {
        timeCountDown?.cancel()
        timeCountDown = object : CountDownTimer(currentModel.getTime().toLong(), 1000) {
            override fun onFinish() {
                finishedTimer.postValue(true)
                currentModel = currentModel.copy(isStarted = false)
                oldDigits = 0
            }

            override fun onTick(millisUntilFinished: Long) {
                currentModel = currentModel.updateTimeLong(millisUntilFinished)
                modelLiveData.postValue(currentModel)
            }
        }.start()
    }

    fun stopTimer(context: Context) {
        timeCountDown?.cancel()
        currentModel = currentModel.copy(isStarted = false)
        oldDigits = currentModel.hours * 10000 + currentModel.minutes * 100 + currentModel.seconds
        val intent = Intent(context, TimerService::class.java)
        intent.putExtra("running", false)
        ContextCompat.startForegroundService(context, intent)
        modelLiveData.value = currentModel
    }

    fun restoreTimer() {
        disposable?.dispose()
        disposable = timerStateInteractor.loadFromPreference().subscribe ({
            currentModel = it
            if(it.isStarted)
                start()
            modelLiveData.value = currentModel
        }, {})
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }
}