package com.exploreutapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ui.AppBarConfiguration
import com.exploreutapp.model.Place
import com.exploreutapp.remote.ExploreUTService
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_view_place.*
import java.util.ArrayList


class ViewPlace : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_place)

        val place = intent.getSerializableExtra("place_to_show") as Place

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



        // load photo of place
//        if (place!!.pics != null && place!!.pics!!.isNotEmpty()) {
//            Picasso.get()
//                .load(IExploreUTService.baseURL + "/place_image/" + id + "/" + imageId + ".jpg")
//                .into(photo)
//        }


        Picasso.get().load(ExploreUTService.baseURL + "/place_image/" + id + "/" + imageId + ".jpg")
            .resize(480, 0).into(photo)

        // load reviews
        if (place.reviews != null && place.reviews!!.isNotEmpty()) {
            for (i in 0 until place.reviews!!.size) {
                place_reviews.text = "Reviews: " + place!!.reviews!![i]
            }
        } else {
            place_reviews.visibility = View.GONE
        }

        // load address
        if (place.address != null) {
            place_address.text = place!!.address
        } else {
            place_address.visibility = View.GONE
        }

        // load introduction
        if (place.intro != null) {
            place_intro.text = "Introduction: " + place!!.intro
        } else {
            place_intro.visibility = View.GONE
        }
    }

    companion object {

        val user = User(email = "", _id = "", username = "", name = "",
            profile = "", gender = "", age = 0, group = "",
            level = 0, subscription = ArrayList<String>()
        )
    }

    // override some functions to make navigation bar work
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

    fun addReport(view:View){
        val addReportIntent = Intent(this, CreateReportActivity::class.java)
        startActivity(addReportIntent)
    }
}
