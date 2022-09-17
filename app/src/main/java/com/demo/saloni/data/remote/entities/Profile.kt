package com.demo.saloni.data.remote.entities

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

open class Profile(
    var image:String?="",
    var name:String="",
    var email:String="",
    var phoneNumber:String="",
    val salon:Boolean=false):Serializable{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Profile

        if (image != other.image) return false
        if (name != other.name) return false
        if (email != other.email) return false
        if (phoneNumber != other.phoneNumber) return false
        if (salon != other.salon) return false

        return true
    }

    override fun hashCode(): Int {
        var result = image?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        result = 31 * result + salon.hashCode()
        return result
    }
}