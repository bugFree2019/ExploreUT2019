package com.exploreutapp

import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.exploreutapp.model.Place
import com.exploreutapp.model.User
import com.exploreutapp.remote.ExploreUTService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.pusher.pushnotifications.PushNotifications
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import java.util.ArrayList


class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var menu: Menu? = null
    private val instanceId = "1fabe242-9415-454e-822c-67211e2ebcbc"
    private var disposable: Disposable? = null
    private var allplaces: ArrayList<Place> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view -> onSearchRequested() }

        val drawerLayout: DrawerLayout = findViewById(R.id.container)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_view_all, R.id.nav_map, R.id.nav_add, R.id.nav_manage)
            ,drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        PushNotifications.start(applicationContext, instanceId)
        refreshInterests()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        this.menu = menu
        displayButton()
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        displayButton()
        refreshInterests()

        val interests = PushNotifications.getDeviceInterests()
        Log.d("interests", interests.toString())
    }

    fun displayButton() {
        if (menu != null) {
            val sign_out = menu!!.findItem(R.id.sign_out_button)
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                sign_out.isVisible = false
            } else {
                sign_out.isVisible = true
            }
        }
    }

    private fun refreshInterests() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            PushNotifications.setDeviceInterests(setOf("Place"))
        } else {
            val user = User(
                email = currentUser!!.email!!, _id = "", username = "", name = "",
                profile = "", gender = "", age = 0, group = "",
                level = 0, subscription = ArrayList<String>()
            )
            checkUsers(user)
        }

    }


    private fun checkUsers(user: User) {
        disposable = exploreUTServe.checkUsers(user)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (this::handleResponseTest, this::handleError)
    }

    private fun handleResponseTest(result: ArrayList<Place>) {
        try {
            allplaces = result
            val setSubscribed = mutableSetOf<String>()
            for (p in allplaces) {
                setSubscribed.add(p._id)
            }
            Log.d("user", setSubscribed.toString())

            PushNotifications.setDeviceInterests(setSubscribed)
            PushNotifications.addDeviceInterest("Place")

        } catch (e: JSONException) {
            e.printStackTrace()
            Log.d("myTag", "No valid json")
        }
        Log.d("myTag", "Done")
    }

    private fun handleError(error: Throwable) {
        Log.d("myTag", error.localizedMessage!!)
    }

    companion object {
        val exploreUTServe by lazy {
            ExploreUTService.create()
        }
    }
}