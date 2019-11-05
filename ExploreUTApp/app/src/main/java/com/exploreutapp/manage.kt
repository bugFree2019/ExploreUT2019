package com.exploreutapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ui.AppBarConfiguration
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_manage.*
import java.util.*

class manage : AppCompatActivity() {


    lateinit var providers: List<AuthUI.IdpConfig>
    val MY_REQUEST_CODE: Int = 7117
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
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
            AuthUI.getInstance().signOut(this@manage)
                .addOnCompleteListener{
                    btn_sign_out.isEnabled=false
                    showSignInOptions()
                }
                .addOnFailureListener{
                        e-> Toast.makeText(this@manage,e.message, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun checkUsers(user: User) {
        var disposable: Disposable? = SearchActivity.exploreUTServe.checkUsers(user)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (this::handleResponseTest, this::handleError)
    }

    private fun handleResponseTest(result: User) {

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

        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
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