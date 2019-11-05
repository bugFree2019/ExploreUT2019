package com.exploreutapp.common

import com.exploreutapp.model.Results
import com.exploreutapp.remote.IExploreUTService
import com.exploreutapp.remote.RetrofitClient

object Common {

    private val BASE_URL="http://10.0.2.2:8080"

    var currentResult:Results?=null

    val exploreUtService : IExploreUTService
        get() = RetrofitClient.getClient(BASE_URL).create(IExploreUTService::class.java)

}