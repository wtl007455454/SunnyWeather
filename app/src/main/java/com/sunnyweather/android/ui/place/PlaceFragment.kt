package com.sunnyweather.android.ui.place

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunnyweather.android.MainActivity
import com.sunnyweather.android.R
import com.sunnyweather.android.ui.weather.WeatherActivity
import kotlinx.android.synthetic.main.fragment_place.*

class PlaceFragment: Fragment() {

    val viewModel by lazy { ViewModelProvider(this).get(PlaceViewModel::class.java) }
    private lateinit var adapter: PlaceAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_place, container, false)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (activity is MainActivity && (viewModel.isPlaceSaved("地点一") or viewModel.isPlaceSaved("地点二") or
                    viewModel.isPlaceSaved("地点三"))) {
//            val place = viewModel.getSavedPlace()
//            val intent = Intent(context, WeatherActivity::class.java).apply {
//                putExtra("location_lng", place.location.lng)
//                putExtra("location_lat", place.location.lat)
//                putExtra("place_name", place.name)
//            }
            val intent = Intent(context,WeatherActivity::class.java)
            if(viewModel.isPlaceSaved("地点一")){
                val place = viewModel.getSavedPlace("地点一")
                intent.apply {
                    putExtra("location_lng_0",place.location.lng)
                    putExtra("location_lat_0", place.location.lat)
                    putExtra("place_name_0", place.name)
                }
            }
            if(viewModel.isPlaceSaved("地点二")){
                val place = viewModel.getSavedPlace("地点二")
                intent.apply {
                    putExtra("location_lng_1",place.location.lng)
                    putExtra("location_lat_1", place.location.lat)
                    putExtra("place_name_1", place.name)
                }
            }
            if(viewModel.isPlaceSaved("地点三")){
                val place = viewModel.getSavedPlace("地点三")
                intent.apply {
                    putExtra("location_lng_2",place.location.lng)
                    putExtra("location_lat_2", place.location.lat)
                    putExtra("place_name_2", place.name)
                }
            }
            startActivity(intent)
            activity?.finish()
            return
        }

        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        adapter = PlaceAdapter(this, viewModel.placeList)
        recyclerView.adapter = adapter
        searchPlaceEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val content = p0.toString()
                if (content.isNotEmpty()) {
                    viewModel.searchPlaces(content)
                } else {
                    recyclerView.visibility = View.GONE
                    bgImageView.visibility = View.VISIBLE
                    viewModel.placeList.clear()
                    adapter.notifyDataSetChanged()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        viewModel.placeLiveData.observe(viewLifecycleOwner, Observer{ result ->
            val places = result.getOrNull()
            if (places != null) {
                recyclerView.visibility = View.VISIBLE
                bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
    }

}