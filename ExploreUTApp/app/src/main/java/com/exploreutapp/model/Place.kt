package com.exploreutapp.model

import android.provider.Contacts
import java.io.Serializable

class Place: Serializable {
    var name: String = ""
    var _id: String = ""
    var theme: String = ""
    var tags: ArrayList<String> = ArrayList()
    var address: String = ""
    var reviews: ArrayList<String> = ArrayList()
    var intro: String? = ""
    var location: Location = Location()
    var num_pics:Int = 0
    var subscribe_status: Int = -1
    var likes: Int = 0
 //   var website:String?=null

}