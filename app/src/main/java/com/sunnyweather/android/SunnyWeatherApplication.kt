package com.sunnyweather.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class SunnyWeatherApplication: Application() {

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        const val TOKEN = "QrLmVeVgA4j3MxZi"
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}
