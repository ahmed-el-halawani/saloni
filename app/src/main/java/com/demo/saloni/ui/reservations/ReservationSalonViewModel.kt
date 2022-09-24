package com.demo.saloni.ui.reservations

import androidx.lifecycle.ViewModel
import com.demo.saloni.data.local.CashedData
import com.demo.saloni.data.remote.SalonServices
import com.demo.saloni.data.remote.entities.Barber
import com.demo.saloni.data.remote.entities.Reservation
import com.demo.saloni.ui.core.State
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import java.util.*
import java.util.concurrent.Flow

class ReservationSalonViewModel : ViewModel() {
    val salonProfile = CashedData.salonProfile;

    val salonServices = SalonServices.getInstance()

    val calender = Calendar.getInstance()


    fun getBarbers(salonId: String) = salonServices.getBarbers(salonId)

    fun getReservations(barberId: String) = salonServices.getReservation(barberId)
    fun getReports(barberId: String) = salonServices.getReports(barberId)

}