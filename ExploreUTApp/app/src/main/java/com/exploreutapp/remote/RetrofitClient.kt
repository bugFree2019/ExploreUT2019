package com.exploreutapp.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder


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

}