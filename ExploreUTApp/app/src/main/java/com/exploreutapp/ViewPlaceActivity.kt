package com.exploreutapp

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ui.AppBarConfiguration
import com.exploreutapp.model.Place
import com.exploreutapp.remote.ExploreUTService
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_view_place.*
import org.json.JSONException
import java.io.Serializable


class ViewPlaceActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var place:Place
    private var user: FirebaseUser? = null
    private var disposable: Disposable? = null
    private var disposable2: Disposable? = null
    private var disposable3: Disposable? = null

    companion object {
        val exploreUTServe by lazy {
            ExploreUTService.create()
        }
        private const val TAG = "ViewPlace"
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

        // Set empty for simple text views
        place_address.text=""
        place_intro.text=""
        place_likes.text = "0"

        showUI()

        // get reference to button
        val place_share = findViewById(R.id.place_share) as TextView
        // set on-click listener
        place_share.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "https://explore-ut.appspot.com/view_one_place?place_id=" + place!!._id)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
            
        }
    }

    override fun onResume() {
        super.onResume()
        view_place()
    }

    // override some functions to make navigation bar work
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        val sign_out = menu.findItem(R.id.sign_out_button)
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            sign_out.setVisible(false)
        }
        else {
            sign_out.setVisible(true)
        }
        return true
    }

    // for back button navigation
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        if(item.getItemId() == R.id.sign_out_button) {
            //Signout
            AuthUI.getInstance().signOut(this).addOnCompleteListener{
            }.addOnFailureListener{
                Log.d("myTag", "sign out error")
            }
            item.setVisible(false)
            user = null
            hideButtons()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
        disposable2?.dispose()
        disposable3?.dispose()
    }

    private fun showUI() {
        // load photo to GridView adapter
        var gridview = findViewById<GridView>(R.id.gridview)
        var g_adapter = GridViewAdapterShowPhotos(this, place)
        gridview.adapter = g_adapter

        // load reviews
        if (place.reviews != null && place.reviews.isNotEmpty()) {
            var listview = findViewById<ListView>(R.id.list)
            var l_adapter = ListViewAdapter(this, place.reviews)
            listview.adapter = l_adapter
        } else {
//            list.visibility = View.GONE
        }

        place_likes.text = place.likes.toString()

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

    private fun view_place() {
        if (user != null) {
            disposable = exploreUTServe.getOnePlace(place._id, user!!.email!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse, this::handleError)
        }
        else {
            disposable = exploreUTServe.getOnePlace(place._id, null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse, this::handleError)
        }
    }

    fun onSubscribe(view: View) {
        Log.d("myTag", "on subscribe")
        if (user != null) {
            val subscribeButton = findViewById<View>(R.id.subscribe_button) as Button
            val unsubscribeButton = findViewById<View>(R.id.unsubscribe_button) as Button
            subscribeButton.setVisibility(View.INVISIBLE)
            unsubscribeButton.setVisibility(View.VISIBLE)
            place_likes.text = (Integer.parseInt(place_likes.text.toString()) + 1).toString()

            disposable2 = exploreUTServe.subscribe(place._id, user!!.email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (this::handleResponseSubscribe, this::handleError)

            // notification subscription
            FirebaseMessaging.getInstance().subscribeToTopic(place.name)
                .addOnCompleteListener { task ->
                    var msg = getString(R.string.place_subscribed)
                    if (!task.isSuccessful) {
                        msg = getString(R.string.place_subscribe_failed)
                    }
                    Log.d(TAG, msg)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun onUnsubscribe(view: View) {
        Log.d("myTag", "on unsubscribe")
        if (user != null) {
            val subscribeButton = findViewById<View>(R.id.subscribe_button) as Button
            val unsubscribeButton = findViewById<View>(R.id.unsubscribe_button) as Button
            subscribeButton.setVisibility(View.VISIBLE)
            unsubscribeButton.setVisibility(View.INVISIBLE)
            place_likes.text = (Integer.parseInt(place_likes.text.toString()) - 1).toString()

            disposable3 = exploreUTServe.unsubscribe(place._id, user!!.email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseUnsubscribe, this::handleError)

            // notification unsubscribe
            FirebaseMessaging.getInstance().unsubscribeFromTopic(place.name)
                .addOnCompleteListener { task ->
                    var msg = getString(R.string.place_unsubscribed)
                    if (!task.isSuccessful) {
                        msg = getString(R.string.place_unsubscribe_failed)
                    }
                    Log.d(TAG, msg)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun addReport(view:View){
        val addReportIntent = Intent(this, CreateReportActivity::class.java)
        addReportIntent.putExtra("place", place as Serializable)
        startActivity(addReportIntent)
    }

    private fun handleResponse(result: Place) {
        try {
            place = result
            showUI()
            val subscribeButton = findViewById<View>(R.id.subscribe_button) as Button
            val unsubscribeButton = findViewById<View>(R.id.unsubscribe_button) as Button
            val addButton = findViewById<View>(R.id.button_report) as Button

            if (user != null) {
                Log.d("myTag", "logged in")
                Log.d("myTag", user!!.email!!)
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
                hideButtons()
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

    private fun hideButtons() {
        val subscribeButton = findViewById<View>(R.id.subscribe_button) as Button
        val unsubscribeButton = findViewById<View>(R.id.unsubscribe_button) as Button
        val addButton = findViewById<View>(R.id.button_report) as Button
        subscribeButton.setVisibility(View.INVISIBLE)
        unsubscribeButton.setVisibility(View.INVISIBLE)
        addButton.setVisibility(View.INVISIBLE)
    }

}
