package com.exploreutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.exploreutapp.model.Places
import kotlinx.android.synthetic.main.activity_view_place.*


class ViewPlace : AppCompatActivity() {

//    companion object {
//        var mPlace: Places?=null
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_place)

        // Set empty for all text view

        val place = intent.getSerializableExtra("place_to_show") as Places

        var id = place!!._id
        var imageId = 0

        place_name.text=""
        place_address.text=""
        place_reviews.text=""
        place_intro.text=""

//        btn_show_map.setOnClickListener{
//            // open map intent to view
//            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mPlace!!.result!!.url))
//            startActivity(mapIntent)
//        }


//        // load photo of place
//        if (place!!.pics != null && place!!.pics!!.isNotEmpty()) {
//            Picasso.get()
//                .load(IExploreUTService.baseURL + "/place_image/" + id + "/" + imageId + ".jpg")
//                .into(photo)
//        }

//        // load rating
//        if (Common.currentResult!!.rating != null) rating_bar.rating = Common.currentResult!!.rating.toFloat()
//        else rating_bar.visibility = View.GONE

        // load reviews
        if (place!!.reviews != null) {
            place_reviews.text = "Reviews: " + place!!.reviews!![0]
        } else {
            place_reviews.visibility = View.GONE
        }

        // load address
        if (place!!.name != null) {
            place_name.text = place!!.name
            place_address.text = place!!.address
        } else {
            place_address.visibility = View.GONE
        }

        // load introduction
        if (place!!.intro != null) {
            place_reviews.text = "Introduction: " + place!!.intro
        } else {
            place_intro.visibility = View.GONE
        }

    }
}
