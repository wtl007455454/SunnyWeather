package com.sunnyweather.android.logic.network

import android.telecom.Call
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.DailyResponse
import com.sunnyweather.android.logic.model.HourlyResponse
import com.sunnyweather.android.logic.model.RealtimeResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherService {

    @GET("v2.5/${SunnyWeatherApplication.TOKEN}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(@Path("lng") lng: String, @Path("lat") lat: String): retrofit2.Call<RealtimeResponse>

    @GET("v2.5/${SunnyWeatherApplication.TOKEN}/{lng},{lat}/daily.json")
    fun getDailyWeather(@Path("lng") lng: String, @Path("lat") lat: String): retrofit2.Call<DailyResponse>

    @GET("v2.5/${SunnyWeatherApplication.TOKEN}/{lng},{lat}/hourly.json")
    fun getHourlyWeather(@Path("lng") lng: String,@Path("lat") lat: String): retrofit2.Call<HourlyResponse>

}