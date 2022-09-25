package com.demo.barber.data.remote.entities

import java.io.Serializable
import java.util.*

class Reservation(
    val barberId: String = "",
    val salonId:String = "",
    val services: List<Service> = emptyList(),
    val date:Date?=Date(),
    val paymentMethod: PaymentMethods = PaymentMethods.Cash,
    var reservationId: String = "",
    var client: ClientProfile? = ClientProfile(),
):Serializable


enum class PaymentMethods {
    Cash, Kent
}