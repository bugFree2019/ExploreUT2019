package com.exploreutapp

import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

//        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)
    }

    fun onSearch(view: View) {
        onSearchRequested()
    }
}

interface ExploreUTService {
    @GET("/")
    @Headers("Accept: application/json", "User-Agent: Android")
    fun getAllPlaces() : Observable<ArrayList<Place>>

    @GET("/search")
    @Headers("Accept: application/json", "User-Agent: Android")
    fun getTagPlaces(@Query("tag")  tag: String) : Observable<ArrayList<Place>>

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

data class Place(val _id: String, val name: String = "", val theme: String = "", val tags: ArrayList<String>,
                 val address: String = "", val intro: String = "")
