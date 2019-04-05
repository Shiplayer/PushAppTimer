package ru.shiplayer.pushapptimer.ui

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.lifecycle.*
import kotlinx.android.synthetic.main.activity_main.*
import ru.shiplayer.pushapptimer.R
import ru.shiplayer.pushapptimer.ui.viewmodel.TimerViewModel

class MainActivity : AppCompatActivity(), LifecycleOwner {

    companion object {
        const val TIMER_MODEL_KEY = "timerModelKey"
    }

    private val lifecycleRegistry = LifecycleRegistry(this);

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this)[TimerViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.restoreTimer()
        viewModel.modelLiveData.observe(this, Observer {
            tv_timer_hours.text = it.hours.toTimeFormat() + "ч."
            tv_timer_minutes.text = it.minutes.toTimeFormat() + "м."
            tv_timer_seconds.text = it.seconds.toTimeFormat() + "c."
            if(it.isStarted)
                fa_start.setImageResource(R.drawable.ic_stop_black_24dp)
            else
                fa_start.setImageResource(R.drawable.ic_play_arrow_black_24dp)
        })

        fa_start.setOnClickListener {
            if (viewModel.currentModel.isStarted) {
                fa_start.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                viewModel.stopTimer(this)
            } else {
                fa_start.setImageResource(R.drawable.ic_stop_black_24dp)
                viewModel.startTimer(this)

            }
        }

        viewModel.finishedTimer.observe(this, Observer {
            fa_start.setImageResource(R.drawable.ic_play_arrow_black_24dp)
        })

        button_layout.children.forEach {
            if (it is Button) {
                it.setOnClickListener {
                    if(!viewModel.currentModel.isStarted)
                        viewModel.action((it as Button).text.toString().toInt())
                }
            }
        }

        iv_remove_digit.setOnClickListener {
            if(!viewModel.currentModel.isStarted)
                viewModel.deleteLastDigit()
        }

        iv_remove_digit.setOnLongClickListener {
            if(!viewModel.currentModel.isStarted)
                viewModel.clear()
            true
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        Log.i("MainActivity", "onSaveInstanceState")
        viewModel.saveInstance()
    }
}

fun Int.toTimeFormat(): String {
    return String.format("%02d", this)
}
