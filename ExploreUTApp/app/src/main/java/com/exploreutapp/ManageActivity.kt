package com.exploreutapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exploreutapp.model.Place
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_manage.*
import org.json.JSONException
import java.io.Serializable
import java.util.*

class ManageActivity : AppCompatActivity() {
    private var allplaces: ArrayList<Place> = ArrayList()
    lateinit var providers: List<AuthUI.IdpConfig>
    val MY_REQUEST_CODE: Int = 7117
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle("Manage")
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.manage_container)
        appBarConfiguration = AppBarConfiguration(
            setOf(), drawerLayout
        )

        // enable the back button on top left corner
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);

        providers = Arrays.asList<AuthUI.IdpConfig> (
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        showSignInOptions()

        btn_sign_out.setOnClickListener{
            //Signout
            AuthUI.getInstance().signOut(this@ManageActivity)
                .addOnCompleteListener{
                    btn_sign_out.isEnabled=false
                    showSignInOptions()
                }
                .addOnFailureListener{
                        e-> Toast.makeText(this@ManageActivity,e.message, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun checkUsers(user: User) {
        var disposable: Disposable? = SearchActivity.exploreUTServe.checkUsers(user)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (this::handleResponseTest, this::handleError)
    }

    private fun handleResponseTest(result: ArrayList<Place>) {
        try {
            allplaces = result
            for (p in allplaces) {
                Log.d("myTag", p.name)
            }
            viewManager = LinearLayoutManager(this)
            viewAdapter = RecyclerViewAdapter(allplaces)
            recyclerView = findViewById<RecyclerView>(R.id.my_recycler_view).apply {
                // use this setting to improve performance if you know that changes
                // in content do not change the layout size of the RecyclerView
                setHasFixedSize(true)
                // use a linear layout manager
                layoutManager = viewManager
                // specify an viewAdapter (see also next example)
                adapter = viewAdapter
            }

            // test click events on recycler view
            recyclerView.addOnItemTouchListener(
                RecyclerItemClickListener(
                    this,
                    recyclerView,
                    object : RecyclerItemClickListener.OnItemClickListener {
                        override fun onItemClick(view: View, position: Int) {
                            Log.d("myTag", "$position item clicked")
                            val viewIntent = Intent(this@ManageActivity, ViewPlace::class.java)
                            // start new activity
                            viewIntent.putExtra("place_to_show", allplaces[position] as Serializable)
                            startActivity(viewIntent)
                        }

                        override fun onLongItemClick(view: View, position: Int) {
                            Log.d("myTag", "$position item long clicked")
                            // do whatever
                        }
                    })
            )
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.d("myTag", "No valid json")
        }
        Log.d("myTag", "Done")
    }

    private fun handleError(error: Throwable) {
        Log.d("myTag", error.localizedMessage!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == MY_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if(resultCode == Activity.RESULT_OK) {
                val users = FirebaseAuth.getInstance().currentUser
                println(users!!.email)
                val user = User(email = users!!.email!!, _id = "", username = "", name = "",
                    profile = "", gender = "", age = 0, group = "",
                    level = 0, subscription = ArrayList<String>()

                )
                checkUsers(user)

                Toast.makeText(this,""+users!!.email, Toast.LENGTH_SHORT).show()

                btn_sign_out.isEnabled = true
            } else {
                Toast.makeText(this,""+response!!.error!!.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSignInOptions() {

        startActivityForResult(AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)

            .setTheme(R.style.MyTheme)
            .build(),MY_REQUEST_CODE)
    }

    // override some functions to make navaigation bar work
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    // for back button navigation
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed();
        return true;
    }
}