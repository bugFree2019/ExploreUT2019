package com.exploreutapp

import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface ExploreUTService {
    @GET("/")
    @Headers("Accept: application/json", "User-Agent: Android")
    fun getAllPlaces() : Observable<ArrayList<Place>>

    @GET("/search")
    @Headers("Accept: application/json", "User-Agent: Android")
    fun getTagPlaces(@Query("tag")  tag: String) : Observable<ArrayList<Place>>

    @POST("/posttest")
    @Headers("Accept: application/json", "Content-Type: application/json", "User-Agent: Android")
    fun postTest(@Body place: ArrayList<Place>) : Observable<ArrayList<Place>>

    @POST("/index")
    @Headers("Accept: application/json", "Content-Type: application/json", "User-Agent: Android")
    fun checkUsers(@Body user: User) : Observable<ArrayList<Place>>

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
