package ru.shiplayer.pushapptimer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import ru.shiplayer.pushapptimer.App
import ru.shiplayer.pushapptimer.NotificationHelper
import ru.shiplayer.pushapptimer.interactor.TimerStateInteractor
import ru.shiplayer.pushapptimer.model.TimerModel
import ru.shiplayer.pushapptimer.ui.MainActivity
import javax.inject.Inject


class TimerService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @Inject
    lateinit var interactor: TimerStateInteractor

    private lateinit var timer: TimerModel

    private var countDownTimer: CountDownTimer? = null

    private val notificationHelper by lazy {
        NotificationHelper(baseContext)
    }

    init {
        App.component.inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.extras != null) {
            with(intent.extras!!) {
                if (getBoolean("running", false)) {
                    if (containsKey(MainActivity.TIMER_MODEL_KEY)) {
                        timer = getParcelable(MainActivity.TIMER_MODEL_KEY)!!
                        startTimer()
                    }
                } else
                    stopTimer()
            }
        }
        return START_STICKY
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timer.getTime().toLong(), 1000) {
            override fun onFinish() {
                notificationHelper.timeOutNotification(2, "Time out!")
                interactor.saveInPreference(TimerModel())
                stopSelf()
            }

            override fun onTick(millisUntilFinished: Long) {
                startForeground(1, notificationHelper.getUpdateNotification(timer.updateTimeLong(millisUntilFinished)))
            }

        }
        countDownTimer?.start()
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
        stopSelf()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }


    override fun onCreate() {
        super.onCreate()
        val notification = notificationHelper.getNotification()
        startForeground(1, notification)
        Log.i("RingingService", "service created")
    }
}

