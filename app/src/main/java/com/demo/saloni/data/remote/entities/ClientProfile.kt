package com.demo.saloni.data.remote.entities

import java.io.Serializable

class ClientProfile(
    val userId: String = "",
    image: String? = "",
    name: String = "",
    val civilId: String = "",
    val dataOfBirth: String = "",
    phoneNumber: String = "",
    email: String = "",
    salon: Boolean = false,
    ) : Profile(image, name, email, phoneNumber, salon), Serializable{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as ClientProfile

        if (userId != other.userId) return false
        if (civilId != other.civilId) return false
        if (dataOfBirth != other.dataOfBirth) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + userId.hashCode()
        result = 31 * result + civilId.hashCode()
        result = 31 * result + dataOfBirth.hashCode()
        return result
    }
}