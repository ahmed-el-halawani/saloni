package com.demo.saloni.data.remote.entities

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(val email:String="",val phoneNumber:String=""){


}