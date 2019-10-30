package com.exploreutapp

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit
import org.json.JSONException


interface ExploreUTService {
    @GET("/")
    @Headers("Accept: application/json", "User-Agent: Android")
    fun getAllPlaces() : Observable<ArrayList<Place>>

    companion object {
        // This is the URL when developed on the local emulator
        const val baseURL: String = "http://10.0.2.2:8080"

        fun create(): ExploreUTService {

            val client = OkHttpClient().newBuilder()
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS).build()

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseURL)
                .client(client)
                .build()

            return retrofit.create(ExploreUTService::class.java)
        }
    }
}

class MainActivity : AppCompatActivity() {
    // handle the response with an arraylist of places
    private fun handleResponse(result: ArrayList<Place>) {
        try {
            val textView = findViewById<TextView>(R.id.textView)
            for(r in result) {
                Log.d("myTag", r._id.toString())
                Log.d("myTag", r.name)
                Log.d("myTag", r.theme)
                Log.d("myTag", r.tags.toString())
                Log.d("myTag", r.address)
                Log.d("myTag", r.intro)
                textView.text = r.name
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.d("myTag", "No valid json")
        }
        Log.d("myTag", "Done")
    }

    private fun handleError(error: Throwable) {
        Log.d("myTag", error.localizedMessage)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        // load the image into an image view through Picasso
        val imageView = findViewById<ImageView>(R.id.imageView)
        Picasso.get().load("http://10.0.2.2:8080/place_image/5db8cdbd6e730da24966a327/0.jpg")
            .resize(360, 640).into(imageView)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val exploreUTServe by lazy {
            ExploreUTService.create()
        }

        var disposable: Disposable? = exploreUTServe.getAllPlaces()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (this::handleResponse, this::handleError)
    }
}

data class Place(val _id: HashMap<String, String>, val name: String = "", val theme: String = "", val tags: Array<String>,
                 val address: String = "", val intro: String = "")
