package com.sunnyweather.android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.ui.weather.WeatherActivity
import com.sunnyweather.android.ui.weather.WeatherFragment
import kotlinx.android.synthetic.main.fragment_weather.*

class MyService : Service() {

    lateinit var timeChangeReceiver: TimeChangeReceiver

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("MyService","创建Service")
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("my_service", "前台Service通知",
                NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
//        val intent = Intent(SunnyWeatherApplication.context, WeatherActivity::class.java)
//        // 点击通知图标可以跳转到界面
//        intent.apply {
//            addCategory(Intent.CATEGORY_LAUNCHER)
//            setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
//        }
//        val pi = PendingIntent.getActivity(SunnyWeatherApplication.context, 0, intent, 0)
//        val notification = NotificationCompat.Builder(SunnyWeatherApplication.context, "my_service")
//            .setContentTitle("SunnyWeather")
//            .setContentText("前台Service定时刷新(间隔：${Repository.getSavedTime("targetTime")}分钟)")
//            .setSmallIcon(R.drawable.ic_sunny_weather_logo)
//            .setLargeIcon(BitmapFactory.decodeResource(SunnyWeatherApplication.context.resources, R.drawable.ic_sunny_weather_logo))
//            .setContentIntent(pi)
//            .build()
//        startForeground(1, notification)

        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.TIME_TICK")
        timeChangeReceiver = TimeChangeReceiver()
        registerReceiver(timeChangeReceiver, intentFilter)

        Repository.saveTime("curTime",0)
        Repository.saveTime("targetTime",1)
        Repository.saveService(true)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MyService","启动Service")
        val intent = Intent(SunnyWeatherApplication.context, WeatherActivity::class.java)
        // 点击通知图标可以跳转到界面
        intent.apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        }
        val pi = PendingIntent.getActivity(SunnyWeatherApplication.context, 0, intent, 0)
        val notification = NotificationCompat.Builder(SunnyWeatherApplication.context, "my_service")
            .setContentTitle("SunnyWeather")
            .setContentText("前台Service定时刷新(间隔：${Repository.getSavedTime("targetTime")}分钟)")
            .setSmallIcon(R.drawable.ic_sunny_weather_logo)
            .setLargeIcon(BitmapFactory.decodeResource(SunnyWeatherApplication.context.resources, R.drawable.ic_sunny_weather_logo))
            .setContentIntent(pi)
            .build()
        startForeground(1, notification)

        for(i in 0 until WeatherActivity.fragmentList.size){
            WeatherActivity.fragmentList[i].refreshOpen.text = "间隔刷新开关: 开"
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MyService","停止Service")
        unregisterReceiver(timeChangeReceiver)

        Repository.saveTime("curTime",0)
        Repository.saveTime("targetTime",1)
        Repository.saveService(false)

        for(i in 0 until WeatherActivity.fragmentList.size){
            WeatherActivity.fragmentList[i].refreshOpen.text = "间隔刷新开关: 关"
        }
    }

    inner class TimeChangeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("MyService","1 minmus, ${WeatherActivity.fragmentList.size}")

            val curTime = Repository.getSavedTime("curTime")
            val targetTime = Repository.getSavedTime("targetTime")

            if((curTime + 1) == targetTime){
                for(i in 0 until WeatherActivity.fragmentList.size){
                    Log.d("MyService","${i}")

                    if (WeatherActivity.fragmentList[i].viewModel != null){
                        WeatherActivity.fragmentList[i].refreshWeather()
                        Log.d("MyService","fragment_${i}_ok")
                    }
                }

                Repository.saveTime("curTime",0)
            }else{
                Repository.saveTime("curTime",curTime+1)
            }
        }
    }

}
