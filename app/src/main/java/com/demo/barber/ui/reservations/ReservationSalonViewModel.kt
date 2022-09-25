package com.demo.barber.ui.reservations

import androidx.lifecycle.ViewModel
import com.demo.barber.data.local.CashedData
import com.demo.barber.data.remote.SalonServices
import java.util.*

class ReservationSalonViewModel : ViewModel() {
    val salonProfile = CashedData.salonProfile;

    val salonServices = SalonServices.getInstance()

    val calender = Calendar.getInstance()


    fun getBarbers(salonId: String) = salonServices.getBarbers(salonId)

    fun getReservations(barberId: String) = salonServices.getReservation(barberId)
    fun getReports(barberId: String) = salonServices.getReports(barberId)

}