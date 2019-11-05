package com.example.view.remote

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder
import com.google.gson.Gson



object RetrofitClient {
    private var retrofit: Retrofit?=null

    var gson = GsonBuilder()
        .setLenient()
        .create()


    fun getClient (baseUrll:String):Retrofit{
        if(retrofit==null) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrll)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return retrofit!!
    }

    //            fun create(): IExploreUTService {
//
//            val client = OkHttpClient().newBuilder()
//                .readTimeout(10, TimeUnit.SECONDS)
//                .connectTimeout(10, TimeUnit.SECONDS).build()
//
//            val retrofit = Retrofit.Builder()
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create())
//                .baseUrl(BASE_URL)
//                .client(client)
//                .build()
//
//            return retrofit.create(IExploreUTService::class.java)
//        }

}