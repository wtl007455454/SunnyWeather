package com.sunnyweather.android.logic.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class HourlyResponse(val status: String,val result: Result){

    data class Result(val hourly: Hourly)

    data class Hourly(val description: String,val skycon: List<Skycon>,val temperature: List<Temperature>,
                      val precipitation: List<Precipitation>,val humidity: List<Humidity>,
                      @SerializedName("air_quality") val airQuality: AirQuality)

    data class Skycon(val datetime: Date,val value: String)

    data class Temperature(val datetime: Date,val value: Float)

    data class Precipitation(val datetime: Date,val value: Float)

    data class Humidity(val datetime: Date,val value: Float)

    data class AirQuality(val aqi: List<Aqi>,@SerializedName("pm25") val pm: List<Pm>)

    data class Aqi(val datetime: Date,val value: Value)

    data class Value(val chn: Int,val usa: Int)

    data class Pm(val datetime: Date,val value: Int)

}