package com.sunnyweather.android.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.Place

object TimeDao {

    // key ：curTime,targetTime
    fun saveTime( key: String , time: Int) {
        sharedPreferences().edit {
            putInt(key, time)
        }
    }
    fun getSavedTime( key:String): Int {
        when(key){
            "curTime" -> {
                return sharedPreferences().getInt(key, 0)
            }
            "targetTime" -> {
                return sharedPreferences().getInt(key, 1)
            }
            else -> {
                return sharedPreferences().getInt(key, 1)
            }
        }
    }
    //fun isPlaceSaved() = sharedPreferences().contains("place")
    fun isTimeSaved(key: String): Boolean{
        return sharedPreferences().contains(key)
    }
    private fun sharedPreferences() = SunnyWeatherApplication.context.getSharedPreferences("sunny_weather", Context.MODE_PRIVATE)

}