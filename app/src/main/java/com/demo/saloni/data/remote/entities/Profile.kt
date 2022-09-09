package com.demo.saloni.data.remote.entities

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
open class Profile(
    val image:String?="",

    val name:String="",

    val email:String="",val phoneNumber:String="",val isSalon:Boolean=false):Serializable{


}