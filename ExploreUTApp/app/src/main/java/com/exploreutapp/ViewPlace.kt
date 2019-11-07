package com.exploreutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.exploreutapp.model.Places
import com.exploreutapp.remote.ExploreUTService
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_view_place.*


class ViewPlace : AppCompatActivity() {

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


        Picasso.get().load(ExploreUTService.baseURL + "/place_image/" + id + "/" + imageId + ".jpg")
            .resize(360, 640).into(photo)

        // load reviews
        println("hello world")
        if (place.reviews != null && place.reviews!!.isNotEmpty()) {
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
            place_intro.text = "Introduction: " + place!!.intro
        } else {
            place_intro.visibility = View.GONE
        }
    }
}
