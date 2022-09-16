package com.demo.saloni.ui.salonqr

import androidx.lifecycle.ViewModel
import com.demo.saloni.data.local.CashedData
import com.demo.saloni.data.remote.SalonServices
import com.demo.saloni.data.remote.entities.Barber
import com.demo.saloni.data.remote.entities.Reservation
import com.demo.saloni.ui.core.State
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.Flow

class SalonScanQrViewModel : ViewModel() {
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

    fun getReservations(barberId: String) = flow<State<Reservation>> {
        emit(State.Loading())
        try {
            val reservation = salonServices.readQr(barberId)
            emit(State.Success(reservation))
        } catch (e: Exception) {
            emit(State.Error(e.message ?: e.localizedMessage))
        }

    }

}