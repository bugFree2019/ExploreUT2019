package com.example.view.common

import com.example.view.model.Results
import com.example.view.remote.IExploreUTService
import com.example.view.remote.RetrofitClient
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Common {

    private val BASE_URL="http://10.0.2.2:8080"

    var currentResult:Results?=null

    val exploreUtService : IExploreUTService
        get() = RetrofitClient.getClient(BASE_URL).create(IExploreUTService::class.java)

}