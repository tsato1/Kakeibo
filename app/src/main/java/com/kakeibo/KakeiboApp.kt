package com.kakeibo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class KakeiboApp : Application() {

    companion object {
        lateinit var context: KakeiboApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        context = this
    }

}