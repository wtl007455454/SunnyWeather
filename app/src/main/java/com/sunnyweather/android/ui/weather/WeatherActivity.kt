package com.sunnyweather.android.ui.weather

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.sunnyweather.android.MyService
import com.sunnyweather.android.R
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.dao.ServiceDao
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
import kotlin.collections.ArrayList

class WeatherActivity : AppCompatActivity() {

    val titleList = listOf<String>("地点一","地点二","地点三")

    companion object{
        val fragmentList = ArrayList<WeatherFragment>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT

        setContentView(R.layout.activity_weather)

        val weatherTabLayout = findViewById<TabLayout>(R.id.weatherTabLayout)
        val weatherViewPager = findViewById<ViewPager>(R.id.weatherViewPager)

        for(i in titleList){
            fragmentList.add(WeatherFragment(i))
        }

        weatherViewPager.adapter = MyAdapter(supportFragmentManager)

        weatherTabLayout.setupWithViewPager(weatherViewPager)

        weatherViewPager.offscreenPageLimit = 2
    }

    override fun onDestroy() {
        super.onDestroy()
        // 最后的activity销毁，顺便杀死进程
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    inner class MyAdapter(fm: FragmentManager): FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
        override fun getCount(): Int {
            return fragmentList.size
        }

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titleList[position]
        }
    }



}
