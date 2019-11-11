package com.exploreutapp.ui.manage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exploreutapp.*
import com.exploreutapp.model.Place
import com.exploreutapp.model.User
import com.exploreutapp.remote.ExploreUTService
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
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

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var recyclerView: RecyclerView
    private var menu: Menu? = null
    private var users: FirebaseUser? = null
    private var disposable: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_manage, container, false)

        setHasOptionsMenu(true)

        users = FirebaseAuth.getInstance().currentUser
        if (users == null) {
            Log.d("myTag", "not logged in")
            showSignInOptions()
        }
        else {
            val user = User(
                email = users!!.email!!, _id = "", username = "", name = "",
                profile = "", gender = "", age = 0, group = "",
                level = 0, subscription = ArrayList<String>()
            )
            Log.d("myTag", users!!.email!!)
            checkUsers(user)
        }

        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == MY_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if(resultCode == Activity.RESULT_OK) {
                users = FirebaseAuth.getInstance().currentUser
                val sign_out = menu!!.findItem(R.id.sign_out_button)
                sign_out.setVisible(true)
                Log.d("myTag", users!!.email!!)
                val user = User(
                    email = users!!.email!!, _id = "", username = "", name = "",
                    profile = "", gender = "", age = 0, group = "",
                    level = 0, subscription = ArrayList<String>()
                )
                checkUsers(user)

                Toast.makeText(context!!,""+users!!.email, Toast.LENGTH_SHORT).show()

            } else {
                if (context != null && response != null) {
                    Toast.makeText(context!!,""+response!!.error!!.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        Log.d("myTag", "menu created")
        val sign_out = menu.findItem(R.id.sign_out_button)
        if (users == null) {
            sign_out.setVisible(false)
        }
        else {
            sign_out.setVisible(true)
        }
        this.menu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        if(item.getItemId() == R.id.sign_out_button) {
            //Signout
            AuthUI.getInstance().signOut(context!!).addOnCompleteListener{
                showSignInOptions()
            }.addOnFailureListener{
                Log.d("myTag", "sign out error")
            }
            item.setVisible(false)
            return true
        }
        return super.onOptionsItemSelected(item)
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
                            val viewIntent = Intent(context!!, ViewPlaceActivity::class.java)
                            // start new activity
                            viewIntent.putExtra("place_to_show", allplaces[position] as Serializable)
                            startActivity(viewIntent)
                        }

                        override fun onLongItemClick(view: View, position: Int) {
                            Log.d("myTag", "$position item long clicked")
                            // do whatever
                            val viewIntent = Intent(context!!, ViewPlaceActivity::class.java)
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
        providers = Arrays.asList<AuthUI.IdpConfig>(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        Log.d("myTag", "sing in option")
        startActivityForResult(AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(false)
            .setTheme(R.style.MyTheme)
            .build(),MY_REQUEST_CODE)
    }

    override fun onResume() {
        Log.d("myTag", "resume")
        super.onResume()
        users = FirebaseAuth.getInstance().currentUser
        if (menu != null && users == null) {
            showSignInOptions()
        }
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}