package com.exploreutapp

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ui.AppBarConfiguration
import com.exploreutapp.model.Place
import com.exploreutapp.remote.ExploreUTService
import java.io.Serializable


class  SearchActivity : AppCompatActivity() {
    companion object {
        val exploreUTServe by lazy {
            ExploreUTService.create()
        }
    }

    private var places: ArrayList<Place> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle("Search Result")
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.search_container)
        appBarConfiguration = AppBarConfiguration(
            setOf(), drawerLayout
        )

        // enable the back button on top left corner
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // Verify the action and get the query
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                search(query)
            }
        }
    }

    private fun search(tag: String) {
        var disposable: Disposable? = exploreUTServe.getTagPlaces(tag)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (this::handleResponse, this::handleError)
    }

    // handle the response with an arraylist of places
    private fun handleResponse(result: ArrayList<Place>) {
        try {
            places = result
            for (p in places) {
                Log.d("myTag", p._id)
            }
            viewManager = LinearLayoutManager(this)
            viewAdapter = RecyclerViewAdapter(places)
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
                            val viewIntent = Intent(this@SearchActivity, ViewPlace::class.java)
                            // start new activity
                            viewIntent.putExtra("place_to_show", places[position] as Serializable)
                            startActivity(viewIntent)
                        }

                        override fun onLongItemClick(view: View, position: Int) {
                            Log.d("myTag", "$position item long clicked")
                            // do whatever
                            val viewIntent = Intent(this@SearchActivity, ViewPlace::class.java)
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

    // override some functions to make navaigation bar work
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    // for back button navigation
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}