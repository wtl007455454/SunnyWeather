package com.sunnyweather.android.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.sunnyweather.android.SunnyWeatherApplication

object ServiceDao {

    // key ：isRunning
    fun saveService(flag: Boolean) {
        sharedPreferences().edit {
            putBoolean("isRunning", flag)
        }
    }

    fun getSavedService(): Boolean {
        return sharedPreferences().getBoolean("isRunning", false)

    }
    private fun sharedPreferences() = SunnyWeatherApplication.context.getSharedPreferences("sunny_weather", Context.MODE_PRIVATE)
}
