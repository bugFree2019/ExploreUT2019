package com.exploreutapp

data class Place(val _id: String, val name: String = "", val theme: String = "", val tags: ArrayList<String>,
                 val address: String = "", val intro: String = "", val encoded_pic: String = "")
