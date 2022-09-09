package com.demo.saloni.data.remote.entities

import java.io.Serializable

class ClientProfile(
    image: String? = "",
    name: String = "",
    val civilId: String = "",
    val dataOfBirth: String = "",
    phoneNumber: String = "",
    email: String = ""
) : Profile(image, name,  email,phoneNumber, false), Serializable