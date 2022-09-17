package com.demo.saloni.ui.salonqr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.saloni.data.local.CashedData
import com.demo.saloni.data.remote.SalonServices
import com.demo.saloni.data.remote.entities.Barber
import com.demo.saloni.data.remote.entities.Reservation
import com.demo.saloni.data.remote.entities.SalonProfile
import com.demo.saloni.ui.core.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.concurrent.Flow

class SalonScanQrViewModel : ViewModel() {
    val salonProfile = CashedData.salonProfile;
    val salonServices = SalonServices.getInstance()
    val reservationState = MutableStateFlow<State<Reservation>>(State.Loading())
    var barber: MutableStateFlow<State<Barber>> = MutableStateFlow(State.Loading())
    var salon: MutableStateFlow<State<SalonProfile>> = MutableStateFlow(State.Loading())

    fun getBarbers(salonId: String) = flow {
        try {
            emit(State.Loading())
            salonServices.getBarbers(salonId).collect { emit(State.Success(it)) }
        } catch (e: Throwable) {
            emit(State.Error(e.message ?: e.localizedMessage))
        }
    }

    fun getReservations(barberId: String) = viewModelScope.launch {
        try {
            val reservation = salonServices.readQr(barberId, salonProfile!!.salonId)
            reservationState.value = State.Success(reservation)
        } catch (e: Exception) {
            reservationState.value = State.Error(e.message ?: e.localizedMessage)
        }
    }


    fun initBarberData(barberId: String) = viewModelScope.launch {
        barber.value = State.Loading()
        try {
            val barberData = salonServices.getBarber(barberId)
            initSalon(barberData.salonId)
            barber.value = State.Success(barberData)
        } catch (e: Throwable) {
            barber.value = State.Error(e.message ?: e.localizedMessage)
        }
    }

    fun initSalon(salonId: String) = viewModelScope.launch {
        salon.value = State.Loading()
        try {
            val salonData = salonServices.getSalon(salonId)
            salon.value = State.Success(salonData)
        } catch (e: Throwable) {
            salon.value = State.Error(e.message ?: e.localizedMessage)
        }
    }

}