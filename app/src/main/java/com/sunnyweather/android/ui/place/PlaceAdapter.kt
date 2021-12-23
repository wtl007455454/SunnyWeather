package com.sunnyweather.android.ui.place

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.ui.weather.WeatherActivity
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.fragment_weather.*

class PlaceAdapter(private val fragment: PlaceFragment, private val placeList: List<Place>) :
    RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val placeName: TextView = view.findViewById(R.id.placeName)
        val placeAddress: TextView = view.findViewById(R.id.placeAddress)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item,
            parent, false)
        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            val place = placeList[position]
            val activity = fragment.activity
            val position1 = activity?.weatherTabLayout?.selectedTabPosition
            if (activity is WeatherActivity) {
                WeatherActivity.fragmentList[position1?:0].drawerLayout.closeDrawers()
                WeatherActivity.fragmentList[position1?:0].viewModel.locationLng = place.location.lng
                WeatherActivity.fragmentList[position1?:0].viewModel.locationLat = place.location.lat
                WeatherActivity.fragmentList[position1?:0].viewModel.placeName = place.name
                WeatherActivity.fragmentList[position1?:0].refreshWeather()
            } else {
                val intent = Intent(parent.context, WeatherActivity::class.java).
                    apply {
                        putExtra("location_lng_0", place.location.lng)
                        putExtra("location_lat_0", place.location.lat)
                        putExtra("place_name_0", place.name)
                    }
                fragment.startActivity(intent)
                activity?.finish()
            }
            //fragment.viewModel.savePlace(place)
            when(position1){
                0 -> {
                    fragment.viewModel.savePlace("地点一",place)
                }
                1 -> {
                    fragment.viewModel.savePlace("地点二",place)
                }
                2 -> {
                    fragment.viewModel.savePlace("地点三",place)
                }
                else ->{
                    fragment.viewModel.savePlace("地点一",place)
                }
            }
        }
        return holder
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.placeName.text = place.name
        holder.placeAddress.text = place.address
    }
    override fun getItemCount() = placeList.size
}
