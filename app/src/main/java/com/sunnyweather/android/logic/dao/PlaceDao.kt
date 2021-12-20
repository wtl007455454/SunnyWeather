package com.sunnyweather.android.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.Place

object PlaceDao {

    // key ：地点一、地点二、地点三
    fun savePlace( key: String , place: Place) {
        sharedPreferences().edit {
            putString(key, Gson().toJson(place))
        }
    }
    fun getSavedPlace( key:String): Place {
        val placeJson = sharedPreferences().getString(key, "")
        return Gson().fromJson(placeJson, Place::class.java)
    }
    //fun isPlaceSaved() = sharedPreferences().contains("place")
    fun isPlaceSaved(key: String): Boolean{
        return sharedPreferences().contains(key)
    }
    private fun sharedPreferences() = SunnyWeatherApplication.context.getSharedPreferences("sunny_weather", Context.MODE_PRIVATE)

}