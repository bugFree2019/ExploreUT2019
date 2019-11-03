package com.example.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.view.Common.Common
import com.example.view.Model.PlaceDetail
import com.example.view.Remote.IGoogleAPIService
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_view_place.*
import retrofit2.Call
import retrofit2.Response


class ViewPlace : AppCompatActivity() {

    internal lateinit var  mService:IGoogleAPIService
    var mPlace:PlaceDetail?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_place)

        // Init service
        mService = Common.googleApiService

        // Set empty for all text view

        place_name.text=""
        place_address.text=""
        place_open_hour.text=""

        btn_show_map.setOnClickListener{
            // open map intent to view
            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mPlace!!.result!!.url))
            startActivity(mapIntent)
        }

        // load photo of place
        if (Common.currentResult!!.photos != null && Common.currentResult!!.photos!!.isNotEmpty()) {
            Picasso.get()
                .load(getPhotoOfPlace(Common.currentResult!!.photos!![0].photo_reference!!,1000))
                .into(photo)
        }

//        // load rating
//        if (Common.currentResult!!.rating != null) rating_bar.rating = Common.currentResult!!.rating.toFloat()
//        else rating_bar.visibility = View.GONE

//        // load open hours
//        if (Common.currentResult!!.opening_hour != null) {
//            place_open_hour.text = "Open now: " + Common.currentResult!!.opening_hour!!.open_now
//        } else {
//            place_open_hour.visibility = View.GONE
//        }

        // use service to fetch address and name
        mService.getDetailPlace(getPlaceDetailUrl(Common.currentResult!!.place_id!!))
            .enqueue(object: retrofit2.Callback<PlaceDetail> {
                override fun onFailure(call: Call<PlaceDetail>, t: Throwable) {
                    Toast.makeText(baseContext, "" + t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<PlaceDetail>, response: Response<PlaceDetail>) {
                    mPlace = response.body()

                    place_address.text = mPlace!!.result!!.formatted_address
                    place_name.text = mPlace!!.result!!.name

                }
            })
    }

    private fun getPlaceDetailUrl(place_id: String): String {

        val url = StringBuilder ("https://maps.googleapis.com/maps/api/place/details/json")
        url.append("?place_id=$place_id")
        url.append("&key=AIzaSyD_H1xRkNuLBh4LP4RzXbZ-LuKVojIka3E")
        return url.toString()
    }

    private fun getPhotoOfPlace(photo_reference: String, maxWidth: Int): String {

        val url = StringBuilder("https://maps.googleapis.com/maps/api/place/photo")
        url.append("?maxwidth=$maxWidth")
        url.append("&photoreference=$photo_reference")
        url.append("&key=AIzaSyD_H1xRkNuLBh4LP4RzXbZ-LuKVojIka3E")
        return url.toString()
    }
}
