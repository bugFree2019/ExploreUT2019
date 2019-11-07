package com.exploreutapp.remote

import com.exploreutapp.model.Places
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Observable
import java.util.concurrent.TimeUnit


interface IExploreUTService {

    @GET("view_one_place")
    @Headers("Accept: application/json", "User-Agent: Android")
    fun getOnePlace(@Query("_id")  _id: String): Observable<Places>

    @GET("/view_places_by_theme")
    @Headers("Accept: application/json", "User-Agent: Android")
    fun getThemePlaces(@Query("theme")  tag: String): Observable<ArrayList<Places>>


    @GET("/search")
    @Headers("Accept: application/json", "User-Agent: Android")
    fun getTagPlaces(@Query("tag")  tag: String) : Observable<ArrayList<Places>>


    companion object {
        // This is the URL when developed on the local emulator
        const val baseURL: String = "http://10.0.2.2:8080"

        fun create(): IExploreUTService {

            val client = OkHttpClient().newBuilder()
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS).build()

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseURL)
                .client(client)
                .build()

            return retrofit.create(IExploreUTService::class.java)
        }
    }
}