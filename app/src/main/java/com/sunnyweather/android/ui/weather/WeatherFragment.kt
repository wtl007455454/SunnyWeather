package com.sunnyweather.android.ui.weather

import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.app.NotificationCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sunnyweather.android.MyService
import com.sunnyweather.android.R
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.model.getSky
import kotlinx.android.synthetic.main.forecast.*
import kotlinx.android.synthetic.main.forecast_hourly.*
import kotlinx.android.synthetic.main.fragment_weather.*
import kotlinx.android.synthetic.main.life_index.*
import kotlinx.android.synthetic.main.now.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class WeatherFragment(val flag: String): Fragment() {

    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_weather,container,false)
//        setHasOptionsMenu(true)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        when(flag){
            "地点一" -> {
                if (viewModel.locationLng.isEmpty()) {
                    viewModel.locationLng = activity?.intent?.getStringExtra("location_lng_0") ?: ""
                }

                if (viewModel.locationLat.isEmpty()) {
                    viewModel.locationLat = activity?.intent?.getStringExtra("location_lat_0") ?: ""
                }

                if (viewModel.placeName.isEmpty()) {
                    viewModel.placeName = activity?.intent?.getStringExtra("place_name_0") ?: ""
                }
            }//121.6544,25.1552
            "地点二" -> {
                if (viewModel.locationLng.isEmpty()) {
                    viewModel.locationLng = activity?.intent?.getStringExtra("location_lng_1") ?: "121.6544"
                }

                if (viewModel.locationLat.isEmpty()) {
                    viewModel.locationLat = activity?.intent?.getStringExtra("location_lat_1") ?: "25.1552"
                }

                if (viewModel.placeName.isEmpty()) {
                    viewModel.placeName = activity?.intent?.getStringExtra("place_name_1") ?: "默认"
                }
            }
            "地点三" -> {
                if (viewModel.locationLng.isEmpty()) {
                    viewModel.locationLng = activity?.intent?.getStringExtra("location_lng_2") ?: "121.6544"
                }

                if (viewModel.locationLat.isEmpty()) {
                    viewModel.locationLat = activity?.intent?.getStringExtra("location_lat_2") ?: "25.1552"
                }

                if (viewModel.placeName.isEmpty()) {
                    viewModel.placeName = activity?.intent?.getStringExtra("place_name_2") ?: "默认"
                }
            }
        }
        viewModel.weatherLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(SunnyWeatherApplication.context, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
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
            override fun onDrawerOpened(drawerView: View) {
                Settings.text.clear()
            }
            override fun onDrawerClosed(drawerView: View) {
                // 隐藏键盘的输入法
                val manager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        })

        startClock.setOnClickListener {
            // 设置默认值
            Repository.saveTime("curTime",0)
            Repository.saveTime("targetTime",1)

            Repository.saveService(true)

            val intent = Intent(SunnyWeatherApplication.context,MyService::class.java)
            activity?.startService(intent)
        }

        stopClock.setOnClickListener {
            // 设置默认值
            Repository.saveTime("curTime",0)
            Repository.saveTime("targetTime",1)

            Repository.saveService(false)

            val intent = Intent(SunnyWeatherApplication.context,MyService::class.java)
            activity?.stopService(intent)
        }

        setBtn.setOnClickListener {
            if(!TextUtils.isEmpty(Settings.text)){
                val refreshTime = Settings.text.toString().toInt()
                Log.d("WeatherFragment", "刷新时间：${refreshTime}")
                if (refreshTime >= 1) {
                    if (Repository.isTimeSaved("targetTime") && Repository.getSavedService()) {
                        Repository.saveTime("targetTime", refreshTime)
                        val intent = Intent(SunnyWeatherApplication.context,MyService::class.java)
                        activity?.startService(intent)
                    } else {
                        Toast.makeText(SunnyWeatherApplication.context, "请开启定时刷新", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(SunnyWeatherApplication.context, "输入需要大于等于1", Toast.LENGTH_SHORT)
                        .show()
                }
                drawerLayout.closeDrawers()
            }else{
                Toast.makeText(SunnyWeatherApplication.context, "输入不能为空", Toast.LENGTH_SHORT)
                    .show()
            }
        }
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
            val view = LayoutInflater.from(SunnyWeatherApplication.context).inflate(R.layout.forecast_item,
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

            val view = LayoutInflater.from(SunnyWeatherApplication.context).inflate(R.layout.forecast_hourly_item,
                hourlyforecastLayout,false)

            val hourlyDateInfo = view.findViewById<TextView>(R.id.hourlyDateInfo)
            val hourlySkyIcon = view.findViewById<ImageView>(R.id.hourlySkyIcon)
            val hourlySkyInfo = view.findViewById<TextView>(R.id.hourlySkyInfo)
            val hourlyTemperatureInfo = view.findViewById<TextView>(R.id.hourlyTemperatureInfo)
            val hourlyPrecipitationInfo = view.findViewById<TextView>(R.id.hourlyPrecipitationInfo)
            val hourlyHumidityInfo = view.findViewById<TextView>(R.id.hourlyHumidityInfo)
            val aqiText = view.findViewById<TextView>(R.id.aqiText)
            val pm25Text = view.findViewById<TextView>(R.id.pm25Text)

            val dataFormat = SimpleDateFormat("yyyy-MM-dd HH 时", Locale.getDefault())

            hourlyDateInfo.text = dataFormat.format(hourlyskycon.datetime)
            hourlySkyIcon.setImageResource(getSky(hourlyskycon.value).icon)
            hourlySkyInfo.text = getSky(hourlyskycon.value).info
            hourlyTemperatureInfo.text = "  ${hourlytempe.value.toInt()} ℃"
            hourlyPrecipitationInfo.text = "${hourlypreci.value} mm/h"
            hourlyHumidityInfo.text = "${(hourlyhumi.value * 100).toInt()} %"
            aqiText.text = "国标：${hourlyAqi.value.chn}"
            pm25Text.text = "${hourpm.value} μg/m3"

            hourlyforecastLayout.addView(view)
        }

        weatherLayout.visibility = View.VISIBLE
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.toolbar,menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when(item.itemId){
//            R.id.startService -> {
//                Toast.makeText(context,"startService",Toast.LENGTH_SHORT).show()
//            }
//            R.id.stopService -> {
//                Toast.makeText(context,"stopService",Toast.LENGTH_SHORT).show()
//            }
//        }
//        return true
//    }



}

