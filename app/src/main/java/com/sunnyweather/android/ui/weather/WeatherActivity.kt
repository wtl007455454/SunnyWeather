package com.sunnyweather.android.ui.weather

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.Sky
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.model.getSky
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.forecast.*
import kotlinx.android.synthetic.main.forecast_hourly.*
import kotlinx.android.synthetic.main.forecast_hourly_item.*
import kotlinx.android.synthetic.main.life_index.*
import kotlinx.android.synthetic.main.now.*
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT

        setContentView(R.layout.activity_weather)

        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }

        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }

        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }

        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            swipeRefresh.isRefreshing = false
        })

        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)

        refreshWeather()

        swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }

        navBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {}
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {
                // 隐藏键盘的输入法
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        })
    }

    fun refreshWeather() {
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        swipeRefresh.isRefreshing = true
    }

    private fun showWeatherInfo(weather: Weather) {

        placeName.text = viewModel.placeName

        val realtime = weather.realtime

        val daily = weather.daily

        val hourly = weather.hourly
        // 填充now.xml布局中的数据
        val currentTempText = "${realtime.temperature.toInt()} ℃"
        currentTemp.text = currentTempText
        currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        currentAQI.text = currentPM25Text
        nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        // 填充forecast.xml布局中的数据
        forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item,
                forecastLayout, false)
            val dateInfo = view.findViewById(R.id.dateInfo) as TextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById(R.id.temperatureInfo) as TextView
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            temperatureInfo.text = tempText
            forecastLayout.addView(view)
        }
        // 填充life_index.xml布局中的数据
        val lifeIndex = daily.lifeIndex
        coldRiskText.text = lifeIndex.coldRisk[0].desc
        dressingText.text = lifeIndex.dressing[0].desc
        ultravioletText.text = lifeIndex.ultraviolet[0].desc
        carWashingText.text = lifeIndex.carWashing[0].desc

        // 填充forecast_hourly.xml中的数据
        hourlyforecastLayout.removeAllViews()
        val hours = hourly.skycon.size
        for(i in 0 until hours){
            val hourlyskycon = hourly.skycon[i]
            val hourlytempe = hourly.temperature[i]
            val hourlypreci = hourly.precipitation[i]
            val hourlyhumi = hourly.humidity[i]
            val hourlyAqi = hourly.airQuality.aqi[i]
            val hourpm = hourly.airQuality.pm[i]

            val view = LayoutInflater.from(this).inflate(R.layout.forecast_hourly_item,
                hourlyforecastLayout,false)

            val hourlyDateInfo = view.findViewById<TextView>(R.id.hourlyDateInfo)
            val hourlySkyIcon = view.findViewById<ImageView>(R.id.hourlySkyIcon)
            val hourlySkyInfo = view.findViewById<TextView>(R.id.hourlySkyInfo)
            val hourlyTemperatureInfo = view.findViewById<TextView>(R.id.hourlyTemperatureInfo)
            val hourlyPrecipitationInfo = view.findViewById<TextView>(R.id.hourlyPrecipitationInfo)
            val hourlyHumidityInfo = view.findViewById<TextView>(R.id.hourlyHumidityInfo)
            val aqiText = view.findViewById<TextView>(R.id.aqiText)
            val pm25Text = view.findViewById<TextView>(R.id.pm25Text)

            val dataFormat = SimpleDateFormat("yyyy-MM-dd HH 时",Locale.getDefault())

            hourlyDateInfo.text = dataFormat.format(hourlyskycon.datetime)
            hourlySkyIcon.setImageResource(getSky(hourlyskycon.value).icon)
            hourlySkyInfo.text = getSky(hourlyskycon.value).info
            hourlyTemperatureInfo.text = "${hourlytempe.value.toInt()} ~ ${hourlytempe.value.toInt()} ℃"
            hourlyPrecipitationInfo.text = "${hourlypreci.value} mm/h"
            hourlyHumidityInfo.text = "${(hourlyhumi.value * 100).toInt()} %"
            aqiText.text = "国标：${hourlyAqi.value.chn}"
            pm25Text.text = "${hourpm.value} μg/m3"

            hourlyforecastLayout.addView(view)
        }

        weatherLayout.visibility = View.VISIBLE
    }
}
