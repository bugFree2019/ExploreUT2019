package com.exploreutapp.model

import java.io.Serializable

class Places: Serializable {
    var name: String? = null
    var _id: String? = null

    var theme: String? = null
    var tags: ArrayList<String>? = null
    var address: String? = null
    var reviews: ArrayList<String>? = null
    var intro: String? = null
    var location: Location? = null
   // var likes: Int = 0

    //   var pics:ArrayList<Photos>?=null
//    var url:String?=null
//    var website:String?=null

}