package com.exploreutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.exploreutapp.model.Results
import com.exploreutapp.remote.IExploreUTService
import kotlinx.android.synthetic.main.activity_view_place.*


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


//        // load photo of place
//        if (Common.currentResult!!.pics != null && Common.currentResult!!.pics!!.isNotEmpty()) {
//            Picasso.get()
//                .load(IExploreUTService.baseURL + "/place_image/" + id + "/" + imageId + ".jpg")
//                .into(photo)
//        }

//        // load rating
//        if (Common.currentResult!!.rating != null) rating_bar.rating = Common.currentResult!!.rating.toFloat()
//        else rating_bar.visibility = View.GONE

        // load reviews
//        if (Common.currentResult!!.reviews != null) {
//            place_reviews.text = "Reviews: " + Common.currentResult!!.reviews!![0]
//        } else {
//            place_reviews.visibility = View.GONE
//        }
//
//        // load address
//        if (Common.currentResult!!.name != null) {
//            place_name.text = Common.currentResult!!.name
//            place_address.text = Common.currentResult!!.address
//        }

    }
}