package com.demo.saloni.data.remote.entities

import java.io.Serializable

class SalonProfile(
    var salonId:String="",
    val salonImage: String? = "",
    val salonName: String = "",
    phoneNumber: String = "",
    email: String = "",
    val address:String="",
    val facebook: String = "",
    val instagram: String = "",
    val twitter: String = "",
) : Profile(salonImage, salonName, email,phoneNumber, true), Serializable