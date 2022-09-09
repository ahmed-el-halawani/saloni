package com.demo.saloni.data.remote.entities

data class Barber(
    var salonId: String = "",
    var name: String = "",
    var phone: String = "",
    var civilId: String = "",
    var image: String?,
    var workingDays: List<Days> = emptyList(),
    var services: List<Service> = emptyList(),
    var shiftStartIn:String="",
    var shiftEntIn:String=""
)

data class Service(
    var id: ServicesType = ServicesType.HairCut,
    var price: Double = 0.0
)

data class Days(
    var dayIndex: String = "",
    var dayName: String = ""
)


enum class ServicesType(val title: String) {
    Null("No More Service"),HairCut("Hair Cut"), BeardCut("Beard Cut"), Cleaning("Cleaning"), Coloring("Coloring"),
}