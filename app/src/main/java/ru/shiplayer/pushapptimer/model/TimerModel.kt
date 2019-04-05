package ru.shiplayer.pushapptimer.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class TimerModel(
    val hours: Int = 0,
    val minutes: Int = 0,
    val seconds: Int = 30,
    val isStarted: Boolean = false,
    val endTime: Date? = null
): Parcelable {
    fun updateTime(digit: Int): TimerModel {
        if(isStarted)
            throw Throwable("Timer is started")
        return TimerModel((digit / 10000) % 100, (digit / 100) % 100, digit % 100)

    }

    fun updateTimeLong(time: Long): TimerModel{
        val bufTime = time / 1000
        val hours = (bufTime / 3600).toInt()
        val minutes = (bufTime / 60 - hours * 60).toInt()
        val seconds = (bufTime - hours * 3600 - minutes * 60).toInt()
        return copy(hours, minutes, seconds)
    }

    fun String.lastDigit(): String {
        return this.substring(this.length - 2, this.length)
    }
    companion object {
        fun instanceOf(time: Long): TimerModel {
            val bufTime = time / 1000
            val hours = (bufTime / 3600).toInt()
            val minutes = (bufTime / 60 - hours * 60).toInt()
            val seconds = (bufTime - hours * 3600 - minutes * 60).toInt() + 1
            return TimerModel(hours, minutes, seconds)
        }
    }

    fun calculateTime(): TimerModel {
        if(isStarted)
            throw Throwable("Timer is started")
        val totalTime = seconds + minutes * 60 + hours * 60 * 60
        val hours = totalTime / 3600
        val minutes = totalTime / 60 - hours * 60
        val seconds = totalTime - hours * 3600 - minutes * 60
        return TimerModel(hours, minutes, seconds, true, Date(Date().time + totalTime*1000))
    }

    fun getTime() = (seconds + minutes * 60 + hours * 60 * 60)* 1000
}

/*
enum class SelectedTime(val id: Int) {
    SECONDS(0),
    MINUTES(1),
    HOURS(2)
}*/
