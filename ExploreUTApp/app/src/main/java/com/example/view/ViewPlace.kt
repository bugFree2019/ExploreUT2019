package com.example.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.view.common.Common
import com.example.view.model.PlaceDetail
import com.example.view.model.Results
import com.example.view.remote.IExploreUTService
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_view_place.*
import retrofit2.Call
import retrofit2.Response


class ViewPlace : AppCompatActivity() {

    var mPlace: Results?=null

    companion object {
        private const val MY_PERMISSION_CODE: Int = 1000
        val mService by lazy {
            IExploreUTService.create()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_place)

        // Set empty for all text view

        place_name.text=""
        place_address.text=""
        place_reviews.text=""

//        btn_show_map.setOnClickListener{
//            // open map intent to view
//            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mPlace!!.result!!.url))
//            startActivity(mapIntent)
//        }

        // load photo of place
//        if (Common.currentResult!!.pics != null && Common.currentResult!!.pics!!.isNotEmpty()) {
//            Picasso.get()
//                .load(getPhotoOfPlace(Common.currentResult!!.pics!![0].photo_reference!!,1000))
//                .into(photo)
//        }

//        // load rating
//        if (Common.currentResult!!.rating != null) rating_bar.rating = Common.currentResult!!.rating.toFloat()
//        else rating_bar.visibility = View.GONE

        // load reviews
        if (Common.currentResult!!.reviews != null) {
            place_reviews.text = "Reviews: " + Common.currentResult!!.reviews!![0]
        } else {
            place_reviews.visibility = View.GONE
        }
//
        // load address
        if (Common.currentResult!!.name != null) {
            place_name.text = Common.currentResult!!.name
            place_address.text = Common.currentResult!!.address
        }

    }
}
