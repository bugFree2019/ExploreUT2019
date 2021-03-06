package com.exploreutapp.ui.view_all

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exploreutapp.*
import com.exploreutapp.model.Place
import com.exploreutapp.remote.ExploreUTService
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.pusher.pushnotifications.PushNotifications
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import java.io.Serializable


class ViewAllFragment : Fragment() {
    companion object {
        val exploreUTServe by lazy {
            ExploreUTService.create()
        }
    }

    private var places: ArrayList<Place> = ArrayList()
    private var disposable: Disposable? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerViewAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_view_all, container, false)

        setHasOptionsMenu(true)

        viewAll()
        val users = FirebaseAuth.getInstance().currentUser
        if (users != null) {
            println(users!!.email)
            Log.d("myTag", users!!.email)
        }
        return root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        if(item.getItemId() == R.id.sign_out_button) {
            //Signout
            AuthUI.getInstance().signOut(context!!).addOnCompleteListener{
            }.addOnFailureListener{
                Log.d("myTag", "sign out error")
            }
            item.setVisible(false)

            PushNotifications.clearDeviceInterests()
            PushNotifications.addDeviceInterest("Place")
            val interests = PushNotifications.getDeviceInterests()
            Log.d("clearInterest", interests.toString())

            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

    override fun onResume() {
        super.onResume()
        Log.d("myTag", "resume")
        //viewAll()
    }

    private fun viewAll() {
        disposable = exploreUTServe.getAllPlaces()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (this::handleResponse, this::handleError)
    }

    // handle the response with an arraylist of places
    private fun handleResponse(result: ArrayList<Place>) {
        try {
            places = result
            for (r in result) {
                Log.d("myTag", r.name)
            }

            viewManager = LinearLayoutManager(context)
            viewAdapter = RecyclerViewAdapter(places)
            recyclerView = getView()!!.findViewById<RecyclerView>(com.exploreutapp.R.id.my_recycler_view_all).apply {
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
                    context!!,
                    recyclerView,
                    object : RecyclerItemClickListener.OnItemClickListener {
                        override fun onItemClick(view: View, position: Int) {
                            Log.d("myTag", "$position item clicked")
                            val viewIntent = Intent(activity!!, ViewPlaceActivity::class.java)
                            // start new activity
                            viewIntent.putExtra("place_to_show", places[position] as Serializable)
                            startActivity(viewIntent)
                        }

                        override fun onLongItemClick(view: View, position: Int) {
                            Log.d("myTag", "$position item long clicked")
                            val viewIntent = Intent(activity!!, ViewPlaceActivity::class.java)
                            // start new activity
                            viewIntent.putExtra("place_to_show", places[position] as Serializable)
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
}