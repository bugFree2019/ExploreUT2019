package com.example.view.remote

import com.example.view.model.MyPlaces
import com.example.view.model.PlaceDetail
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.http.Url
import retrofit2.Call
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Observable
import java.util.concurrent.TimeUnit


interface IExploreUTService {
    @GET
    fun getNearbyPlaces(@Url url:String): Call<MyPlaces>

    @GET
    fun getDetailPlace(@Url url:String): Call<PlaceDetail>

    @GET("/")
    @Headers("Accept: application/json", "User-Agent: Android")
    fun getAllPlaces() : Observable<MyPlaces>

    @GET("/search")
    @Headers("Accept: application/json", "User-Agent: Android")
    fun getTagPlaces(@Query("tag")  tag: String) : Observable<MyPlaces>


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