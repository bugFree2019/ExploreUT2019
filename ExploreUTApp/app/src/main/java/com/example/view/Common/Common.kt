package com.example.view.Common

import com.example.view.Remote.IGoogleAPIService
import com.example.view.Remote.RetrofitClient

object Common {

    private val GOOGLE_API_URL="https://maps.googleapis.com"

    val googleApiService:IGoogleAPIService
        get()=RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService::class.java)
}