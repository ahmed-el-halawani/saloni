package com.demo.saloni.data.remote.entities

import android.net.Uri
import java.io.Serializable

data class Barber(
    val barberId: String = "",
    var salonId: String = "",
    var name: String = "",
    var phone: String = "",
    var civilId: String = "",
    var image: String? = "",
    var workingDays: List<Days> = emptyList(),
    var services: List<Service> = emptyList(),
    var shiftStartIn: ShiftTime? = ShiftTime(),
    var shiftEntIn: ShiftTime? = ShiftTime()
) : Serializable

data class ShiftTime(
    var hour: String = "",
    var minut: String = "",
    var amOrPm: String = "",
)

fun Barber.update(
    name: String? = null,
    phone: String? = null,
    civilId: String? = null,
    image: String? = null,
    workingDays: List<Days> = emptyList(),
    services: List<Service> = emptyList(),
    shiftStartIn: ShiftTime? = null,
    shiftEntIn: ShiftTime? = null
): Barber {
    return this.apply {
        phone?.also { this.phone = phone }
        civilId?.also { this.civilId = civilId }
        image?.also { this.image = image }
        workingDays.takeIf { workingDays -> workingDays.size != this.workingDays.size || workingDays.any { !this.workingDays.contains(it) } }?.also { this.workingDays = it }
        services.takeIf { services -> services.size != this.services.size || services.any { !this.services.contains(it) } }?.also { this.services = it }
        shiftStartIn?.also { this.shiftStartIn = shiftStartIn }
        shiftEntIn?.also { this.shiftEntIn = shiftEntIn }
        name?.also { this.name = name }
    }
}

data class Service(
    var id: ServicesType = ServicesType.HairCut,
    var price: Double = 0.0
) : Serializable

data class Days(
    var dayIndex: String = "",
    var dayName: String = ""
) : Serializable


enum class ServicesType(val title: String) {
    Null("No More Service"), HairCut("Hair Cut"), BeardCut("Beard Cut"), Cleaning("Cleaning"), Coloring("Coloring"),
}