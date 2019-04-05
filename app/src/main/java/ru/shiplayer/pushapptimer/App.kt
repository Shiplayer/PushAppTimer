package ru.shiplayer.pushapptimer

import android.app.Application
import ru.shiplayer.pushapptimer.dagger.component.AppComponent
import ru.shiplayer.pushapptimer.dagger.component.DaggerAppComponent
import ru.shiplayer.pushapptimer.dagger.models.AppModule

class App : Application(){
    companion object {
        lateinit var component: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        component = buildComponent()

    }

    private fun buildComponent() = DaggerAppComponent.builder().appModule(AppModule(this)).build()
}