package com.demo.saloni.data.remote.entities

import java.io.Serializable

class SalonProfile(
    var salonId: String = "",
    salonImage: String? = "",
    salonName: String = "",
    phoneNumber: String = "",
    email: String = "",
    val address: String = "",
    val facebook: String = "",
    val instagram: String = "",
    val twitter: String = "",

    salon: Boolean = true,
) : Profile(salonImage, salonName, email, phoneNumber, salon), Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as SalonProfile

        if (salonId != other.salonId) return false
        if (address != other.address) return false
        if (facebook != other.facebook) return false
        if (instagram != other.instagram) return false
        if (twitter != other.twitter) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + salonId.hashCode()
        result = 31 * result + address.hashCode()
        result = 31 * result + facebook.hashCode()
        result = 31 * result + instagram.hashCode()
        result = 31 * result + twitter.hashCode()
        return result
    }
}

class Model3D(
    val link: String = "",
    val image: String = ""
) : Serializable