package com.exploreutapp.model

data class User(
                val _id: String, val email: String = "", val username: String = "",
                val name: String = "", val profile: String = "", val gender: String = "",
                val age: Int, val group: String, val level: Int, val subscription: ArrayList<String>
                )