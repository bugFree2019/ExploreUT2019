package com.exploreutapp.ui.manage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exploreutapp.*
import com.exploreutapp.model.Place
import com.exploreutapp.remote.ExploreUTService
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_manage.*
import kotlinx.android.synthetic.main.activity_manage.view.*
import org.json.JSONException
import java.io.Serializable
import java.util.*

class ManageFragment : Fragment() {

    companion object {
        val exploreUTServe by lazy {
            ExploreUTService.create()
        }
    }

    private var allplaces: ArrayList<Place> = ArrayList()
    lateinit var providers: List<AuthUI.IdpConfig>
    val MY_REQUEST_CODE: Int = 7117
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var recyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.activity_manage, container, false)

        val users = FirebaseAuth.getInstance().currentUser

        println(users!!.email)



            providers = Arrays.asList<AuthUI.IdpConfig>(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build()
            )

            showSignInOptions()



        root.btn_sign_out.setOnClickListener{
            //Signout
            AuthUI.getInstance().signOut(context!!).addOnCompleteListener{
                root.btn_sign_out.isEnabled=false
                showSignInOptions()
            }.addOnFailureListener{
                Log.d("myTag", "manage error")
            }
        }

        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == MY_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if(resultCode == Activity.RESULT_OK) {
                val users = FirebaseAuth.getInstance().currentUser
//                println(users!!.email)
                Log.d("myTag", users!!.email)
                val user = User(email = users!!.email!!, _id = "", username = "", name = "",
                    profile = "", gender = "", age = 0, group = "",
                    level = 0, subscription = ArrayList<String>()
                )
                checkUsers(user)

                Toast.makeText(context!!,""+users!!.email, Toast.LENGTH_SHORT).show()

                btn_sign_out.isEnabled = true
            } else {
                Toast.makeText(context!!,""+response!!.error!!.message, Toast.LENGTH_SHORT).show()
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
            viewManager = LinearLayoutManager(context!!)
            viewAdapter = RecyclerViewAdapter(allplaces)
            recyclerView = getView()!!.findViewById<RecyclerView>(R.id.my_recycler_view).apply {
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
                RecyclerItemClickListener(context!!,
                    recyclerView,
                    object : RecyclerItemClickListener.OnItemClickListener {

                        override fun onItemClick(view: View, position: Int) {
                            Log.d("myTag", "$position item clicked")
                            val viewIntent = Intent(context!!, ViewPlace::class.java)
                            // start new activity
                            viewIntent.putExtra("place_to_show", allplaces[position] as Serializable)
                            startActivity(viewIntent)
                        }

                        override fun onLongItemClick(view: View, position: Int) {
                            Log.d("myTag", "$position item long clicked")
                            // do whatever
                            val viewIntent = Intent(context!!, ViewPlace::class.java)
                            // start new activity
                            viewIntent.putExtra("place_to_show", allplaces[position] as Serializable)
                            startActivity(viewIntent)
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

    private fun showSignInOptions() {

        startActivityForResult(AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(false)
            .setTheme(R.style.MyTheme)
            .build(),MY_REQUEST_CODE)
    }

}

