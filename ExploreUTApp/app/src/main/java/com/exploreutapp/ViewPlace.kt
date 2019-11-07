package com.exploreutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ui.AppBarConfiguration
import com.exploreutapp.model.Places
import com.exploreutapp.remote.ExploreUTService
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_view_place.*
import kotlinx.android.synthetic.main.cardview.*


class ViewPlace : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_place)

        val place = intent.getSerializableExtra("place_to_show") as Places

        // set up navigation bar with back button
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle(place.name)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.view_place_container)
        appBarConfiguration = AppBarConfiguration(
            setOf(), drawerLayout
        )

        // enable the back button on top left corner
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // Set empty for all text view
        val id = place!!._id
        var imageId = 0

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
        if (place!!.address != null) {
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

    // override some functions to make navaigation bar work
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    // for back button navigation
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
