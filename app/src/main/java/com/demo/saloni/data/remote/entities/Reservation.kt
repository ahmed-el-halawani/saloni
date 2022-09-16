package com.demo.saloni.data.remote.entities

import java.util.*

class Reservation(
    val barberId: String = "",
    val services: List<Service> = emptyList(),
    val date:Date?=Date(),
    val paymentMethod: PaymentMethods = PaymentMethods.Cash,
    var reservationId: String = "",
    var client: ClientProfile? = ClientProfile(),
)


enum class PaymentMethods {
    Cash, Kent
}