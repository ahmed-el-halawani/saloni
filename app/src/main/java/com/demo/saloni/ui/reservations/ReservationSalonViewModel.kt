package com.demo.saloni.ui.reservations

import androidx.lifecycle.ViewModel
import com.demo.saloni.data.local.CashedData
import com.demo.saloni.data.remote.SalonServices
import com.demo.saloni.data.remote.entities.Barber
import com.demo.saloni.ui.core.State
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.Flow

class ReservationSalonViewModel : ViewModel() {
    val salonProfile = CashedData.salonProfile;

    val salonServices = SalonServices.getInstance()


    fun getBarbers(salonId: String) = flow {
        try {
            emit(State.Loading())
            salonServices.getBarbers(salonId).collect { emit(State.Success(it)) }
        } catch (e: Throwable) {
            emit(State.Error(e.message ?: e.localizedMessage))
        }
    }

    fun getReservations(barberId: String) = salonServices.getReservation(barberId)

}