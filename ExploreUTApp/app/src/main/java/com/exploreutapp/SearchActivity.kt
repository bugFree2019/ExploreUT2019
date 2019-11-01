package com.exploreutapp

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.squareup.picasso.Picasso
import java.lang.Thread.sleep


class SearchActivity : AppCompatActivity() {
    companion object {
        val exploreUTServe by lazy {
            ExploreUTService.create()
        }
    }

    private var places: ArrayList<Place> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    // handle the response with an arraylist of places
    private fun handleResponse(result: ArrayList<Place>) {
        try {
            places = result
            viewManager = LinearLayoutManager(this)
            viewAdapter = MyAdapter(places)
            recyclerView = findViewById<RecyclerView>(R.id.my_recycler_view).apply {
                // use this setting to improve performance if you know that changes
                // in content do not change the layout size of the RecyclerView
                setHasFixedSize(true)

                // use a linear layout manager
                layoutManager = viewManager

                // specify an viewAdapter (see also next example)
                adapter = viewAdapter

            }
            for(r in result) {
                Log.d("myTag", r._id)
                Log.d("myTag", r.name)
                Log.d("myTag", r.theme)
                Log.d("myTag", r.tags.toString())
                Log.d("myTag", r.address)
                Log.d("myTag", r.intro)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Verify the action and get the query
        Log.d("myTag", "search activity")
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                Log.d("myTag", query)
                search(query)
            }
        }


//        val navView: BottomNavigationView = findViewById(R.id.nav_view)

//        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications))
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)
    }

    private fun search(tag: String) {
        var disposable: Disposable? = exploreUTServe.getTagPlaces(tag)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (this::handleResponse, this::handleError)
    }
}

internal class MyAdapter(private val places: ArrayList<Place>) :
    RecyclerView.Adapter<MyAdapter.PlaceViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    class PlaceViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var cv: CardView = itemView.findViewById(R.id.cv)
        var personName: TextView = itemView.findViewById(R.id.person_name)
        var personAge: TextView = itemView.findViewById(R.id.person_age)
        var personPhoto: ImageView = itemView.findViewById(R.id.person_photo)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): PlaceViewHolder {
        val v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false)
        return PlaceViewHolder(v)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.personName.setText(places[position].name)
        holder.personAge.setText(places[position].tags[0])
        var id = "5db8cdbd6e730da24966a327"
        id = places[position]._id
        val imageId = 0
        Picasso.get().load(ExploreUTService.baseURL + "/place_image/" + id + "/" + imageId + ".jpg")
                    .resize(360, 640).into(holder.personPhoto)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = places.size
}