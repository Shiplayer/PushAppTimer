package ru.shiplayer.pushapptimer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import ru.shiplayer.pushapptimer.model.TimerModel
import ru.shiplayer.pushapptimer.ui.MainActivity

class NotificationHelper(val context: Context) {
    private val channelId by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("my_service", "My Background Service")
        } else {
            ""
        }
    }

    private val service by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun updateNotification(id: Int, timerModel: TimerModel) {
        service.notify(
            id, getNotificationBuilder(
                "${timerModel.hours}:${timerModel.minutes}:${timerModel.seconds}"
            ).build()
        )
    }

    fun getUpdateNotification(timerModel: TimerModel) =
        getNotificationBuilder(String.format("%02d:%02d:%02d",timerModel.hours, timerModel.minutes, timerModel.seconds)).build()

    fun timeOutNotification(id: Int, content: String) {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        service.notify(
            id, getNotificationBuilder(
                content
            )
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                .setLights(Color.parseColor("#6C3C8B"), 3000, 3000)
                .setSound(uri)
                .build()
        )
    }

    private fun getNotificationBuilder(content: String): NotificationCompat.Builder {

        val pendingIntent = PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0)
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Timer")
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setContentText(content)
    }

    fun getNotification() = getNotificationBuilder("").build()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        service.createNotificationChannel(chan)
        return channelId
    }
}