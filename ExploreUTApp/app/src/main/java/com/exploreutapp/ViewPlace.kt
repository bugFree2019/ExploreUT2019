package com.exploreutapp

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ui.AppBarConfiguration
import com.exploreutapp.model.Place
import com.exploreutapp.remote.ExploreUTService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_view_place.*
import android.widget.Button
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONException
import java.io.Serializable


class ViewPlace : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var place:Place

    companion object {
        val exploreUTServe by lazy {
            ExploreUTService.create()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_place)

        place = intent.getSerializableExtra("place_to_show") as Place
        view_place(place._id)

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
        place_intro.text=""


        var gridview = findViewById<GridView>(R.id.gridview)
        var g_adapter = GridViewAdapterShowPhotos(this, place)
        gridview.adapter = g_adapter


        // load reviews
        var listview = findViewById<ListView>(R.id.list)
        var l_adapter = ListViewAdapter(this, place.reviews)
        listview.adapter = l_adapter

        // load address
        if (place.address != null) {
            place_address.text = place!!.address
        } else {
            place_address.visibility = View.GONE
        }

        // load introduction
        if (place.intro != null) {
            var tv: TextView = findViewById(R.id.place_intro)
            tv.setMovementMethod(ScrollingMovementMethod())

            place_intro.text = "Introduction: " + place!!.intro
        } else {
            place_intro.visibility = View.GONE
        }
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

    private fun handleResponse(result: Place) {
        try {
            place = result
            val users = FirebaseAuth.getInstance().currentUser
            if (users != null) {
                Log.d("myTag", "logged in")
                Log.d("myTag", users.email!!)
                val subscribeButton = findViewById<View>(R.id.subscribe_button) as Button
                subscribeButton.setVisibility(View.VISIBLE)
                if (place.subscribe_status == 0) {
                    subscribeButton()
                }
                else if (place.subscribe_status == 1) {
                    unsubscribeButton()
                }
            }
            else {
                Log.d("myTag", "not logged in")
                val subscribeButton = findViewById<View>(R.id.subscribe_button) as Button
                subscribeButton.setVisibility(View.INVISIBLE)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.d("myTag", "No valid json")
        }
        Log.d("myTag", "Done")
    }

    private fun handleResponse2(result: Place) {
        unsubscribeButton()
    }

    private fun handleResponse3(result: Place) {
        subscribeButton()
    }

    private fun view_place(place_id: String) {
        Log.d("myTag", "in view_place")
        var disposable: Disposable? = exploreUTServe.getOnePlace(place_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (this::handleResponse, this::handleError)
    }

    fun onSubscribe(place_id: String) {
        var disposable: Disposable? = exploreUTServe.subscribe(place_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (this::handleResponse2, this::handleError)
    }

    fun onUnsubscribe(place_id: String) {
        var disposable: Disposable? = exploreUTServe.unsubscribe(place_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (this::handleResponse3, this::handleError)
    }

    private fun handleError(error: Throwable) {
        Log.d("myTag", error.localizedMessage!!)
    }

    private fun subscribeButton() {
        val subscribeButton = findViewById<View>(R.id.subscribe_button) as Button
        subscribeButton.setOnClickListener {
            onSubscribe(place._id)
        }
        subscribeButton.text = "Subscribe"
    }

    private fun unsubscribeButton() {
        val subscribeButton = findViewById<View>(R.id.subscribe_button) as Button
        subscribeButton.setOnClickListener {
            onUnsubscribe(place._id)
        }
        subscribeButton.text = "Unsubscribe"
    }

    fun addReport(view:View){
        val addReportIntent = Intent(this, CreateReportActivity::class.java)
        addReportIntent.putExtra("place_id", place!!._id);
        Log.d("viewplace",place!!._id)
        startActivity(addReportIntent)
    }
}
