package com.exploreutapp.ui.view_all

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exploreutapp.Place
import com.exploreutapp.R
import com.exploreutapp.ExploreUTService
import com.exploreutapp.RecyclerViewAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONException

class ViewAllFragment : Fragment() {
    companion object {
        val exploreUTServe by lazy {
            ExploreUTService.create()
        }
    }

    private var places: ArrayList<Place> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAllViewModel: ViewAllViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewAllViewModel =
            ViewModelProviders.of(this).get(ViewAllViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_view_all, container, false)
        val recyclerView: RecyclerView = root.findViewById(R.id.my_recycler_view_all)
        viewAll()
//        viewAllViewModel.text.observe(this, Observer {
//            recyclerView
//        })
        return root
    }

    private fun viewAll() {
        var disposable: Disposable? = exploreUTServe.getAllPlaces()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (this::handleResponse, this::handleError)
    }

    // handle the response with an arraylist of places
    private fun handleResponse(result: ArrayList<Place>) {
        try {
            places = result
            Log.d("myTag", "handling response")
            for (r in result) {
                Log.d("myTag", r.name)
            }
            viewManager = LinearLayoutManager(context)
            viewAdapter = RecyclerViewAdapter(places)
            recyclerView = getView()!!.findViewById<RecyclerView>(R.id.my_recycler_view).apply {
                // use this setting to improve performance if you know that changes
                // in content do not change the layout size of the RecyclerView
                setHasFixedSize(true)
                // use a linear layout manager
                layoutManager = viewManager
                // specify an viewAdapter (see also next example)
                adapter = viewAdapter
            }
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