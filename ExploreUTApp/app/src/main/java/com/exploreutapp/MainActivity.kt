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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.RemoteMessage
import com.pusher.pushnotifications.PushNotificationReceivedListener
import com.pusher.pushnotifications.PushNotifications


class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var menu: Menu? = null
    private val instanceId = "1fabe242-9415-454e-822c-67211e2ebcbc"

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
        PushNotifications.addDeviceInterest("Place")

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

        PushNotifications.setOnMessageReceivedListenerForVisibleActivity(this, object:
            PushNotificationReceivedListener {
            override fun onMessageReceived(remoteMessage: RemoteMessage) {
                val messagePayload : String? = remoteMessage.data["inAppNotificationMessage"]
                if (messagePayload == null) {
                   Log.i("MyActivity", "Payload was missing")
                } else {
                   Log.i("MyActivity", messagePayload)
                   // Now update the UI based on your message payload!  
                }
            }
        })
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

}