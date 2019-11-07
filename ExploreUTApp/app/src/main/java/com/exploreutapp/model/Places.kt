package com.exploreutapp.model

import java.io.Serializable

class Places: Serializable {
    var name: String = ""
    var _id: String = ""

    var theme: String = ""
    var tags: ArrayList<String> = ArrayList()
    var address: String = ""
    var reviews: ArrayList<String> = ArrayList()
    var intro: String? = ""
    var location: Location = Location()
//    var likes: Int = 0

//    var pics:ArrayList<Photos>?=null
//    var url:String?=null
//    var website:String?=null

}