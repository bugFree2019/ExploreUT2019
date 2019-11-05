package com.exploreutapp.model

import java.io.Serializable

class Places: Serializable {
    var name:String?=null
    var _id:String?=null
    var location:Location?=null
    var theme:String?=null
    var tags:ArrayList<String>?=null
    var address:String?=null
    var reviews:Array<String>?=null
    var intro:String?=null

    //   var pics:ArrayList<Photos>?=null
//    var likes:Int=0

//    var url:String?=null
//    var website:String?=null
}