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
import com.google.firebase.auth.FirebaseUser
import org.json.JSONException
import java.io.Serializable


class ViewPlace : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var place:Place
    private var user: FirebaseUser? = null

    companion object {
        val exploreUTServe by lazy {
            ExploreUTService.create()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_place)

        place = intent.getSerializableExtra("place_to_show") as Place
        user = FirebaseAuth.getInstance().currentUser
        view_place()

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

    private fun view_place() {
        val localUser = user
        if (localUser != null) {
            var disposable: Disposable? = exploreUTServe.getOnePlace(place._id, localUser.email!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse, this::handleError)
        }
        else {
            var disposable: Disposable? = exploreUTServe.getOnePlace(place._id, null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse, this::handleError)
        }
    }

    fun onSubscribe(view: View) {
        Log.d("myTag", "on subscribe")
        val localUser = user
        if (localUser != null) {
            val subscribeButton = findViewById<View>(R.id.subscribe_button) as Button
            val unsubscribeButton = findViewById<View>(R.id.unsubscribe_button) as Button
            subscribeButton.setVisibility(View.INVISIBLE)
            unsubscribeButton.setVisibility(View.VISIBLE)

            var disposable: Disposable? = exploreUTServe.subscribe(place._id, localUser.email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (this::handleResponseSubscribe, this::handleError)
        }
    }

    fun onUnsubscribe(view: View) {
        Log.d("myTag", "on unsubscribe")
        val localUser = user
        if (localUser != null) {
            val subscribeButton = findViewById<View>(R.id.subscribe_button) as Button
            val unsubscribeButton = findViewById<View>(R.id.unsubscribe_button) as Button
            subscribeButton.setVisibility(View.VISIBLE)
            unsubscribeButton.setVisibility(View.INVISIBLE)

            var disposable: Disposable? = exploreUTServe.unsubscribe(place._id, localUser.email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseUnsubscribe, this::handleError)
        }
    }

    fun addReport(view:View){
        val addReportIntent = Intent(this, CreateReportActivity::class.java)
        addReportIntent.putExtra("place_id", place!!._id);
        Log.d("viewplace",place!!._id)
        startActivity(addReportIntent)
    }

    private fun handleResponse(result: Place) {
        try {
            place = result
            val localUser = user
            val subscribeButton = findViewById<View>(R.id.subscribe_button) as Button
            val unsubscribeButton = findViewById<View>(R.id.unsubscribe_button) as Button
            val addButton = findViewById<View>(R.id.button_report) as Button

            if (localUser != null) {
                Log.d("myTag", "logged in")
                Log.d("myTag", localUser.email!!)
                Log.d("myTag", "subscribe status: " + place.subscribe_status.toString())

                if (place.subscribe_status == 0) {
                    subscribeButton.setVisibility(View.VISIBLE)
                    unsubscribeButton.setVisibility(View.INVISIBLE)
                }
                else if (place.subscribe_status == 1) {
                    subscribeButton.setVisibility(View.INVISIBLE)
                    unsubscribeButton.setVisibility(View.VISIBLE)
                }
                addButton.setVisibility(View.VISIBLE)
            }
            else {
                Log.d("myTag", "not logged in")
                subscribeButton.setVisibility(View.INVISIBLE)
                addButton.setVisibility(View.INVISIBLE)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.d("myTag", "No valid json")
        }
        Log.d("myTag", "Done")
    }

    private fun handleResponseSubscribe(result: Place) {

    }

    private fun handleResponseUnsubscribe(result: Place) {

    }

    private fun handleError(error: Throwable) {
        Log.d("myTag", error.localizedMessage!!)
    }
}
