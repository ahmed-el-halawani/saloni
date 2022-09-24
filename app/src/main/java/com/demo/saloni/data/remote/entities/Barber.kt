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
    var shiftEntIn: ShiftTime? = ShiftTime(),
    val model3DFirstStyle: Model3D? = Model3D("https://sketchfab.com/models/0e85a8381b6642b5b2426b7a9585f3c4/embed?autostart=1", ""),
    val model3DSecondStyle: Model3D? = Model3D("https://sketchfab.com/models/ada7267d94d641b092382825b3c0ac85/embed?autostart=1", ""),
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Barber

        if (barberId != other.barberId) return false
        if (salonId != other.salonId) return false
        if (name != other.name) return false
        if (phone != other.phone) return false
        if (civilId != other.civilId) return false
        if (image != other.image) return false
        if (workingDays != other.workingDays) return false
        if (services != other.services) return false
        if (shiftStartIn != other.shiftStartIn) return false
        if (shiftEntIn != other.shiftEntIn) return false

        return true
    }

    override fun hashCode(): Int {
        var result = barberId.hashCode()
        result = 31 * result + salonId.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + phone.hashCode()
        result = 31 * result + civilId.hashCode()
        result = 31 * result + (image?.hashCode() ?: 0)
        result = 31 * result + workingDays.hashCode()
        result = 31 * result + services.hashCode()
        result = 31 * result + (shiftStartIn?.hashCode() ?: 0)
        result = 31 * result + (shiftEntIn?.hashCode() ?: 0)
        return result
    }
}

data class ShiftTime(
    var hour: String = "",
    var minut: String = "",
    var amOrPm: String = "",
) : Serializable

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
    Null("Select Service"), HairCut("Hair Cut"), BeardCut("Beard Cut"), Cleaning("Cleaning"), Coloring("Coloring"),
}