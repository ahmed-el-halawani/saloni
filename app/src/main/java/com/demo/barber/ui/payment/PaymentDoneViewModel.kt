package com.demo.barber.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.barber.data.local.CashedData
import com.demo.barber.data.remote.ClientServices
import com.demo.barber.data.remote.Keys
import com.demo.barber.data.remote.Keys.reservations
import com.demo.barber.data.remote.SalonServices
import com.demo.barber.data.remote.entities.Barber
import com.demo.barber.data.remote.entities.Reservation
import com.demo.barber.data.remote.entities.SalonProfile
import com.demo.barber.ui.core.State
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PaymentDoneViewModel : ViewModel() {
    val clientProfile = CashedData.clientProfile;
    val clientServices = ClientServices.getInstance()
    val salonServices = SalonServices.getInstance()
    var reservationFlow: MutableStateFlow<State<Reservation>>? = null
    var barber: MutableStateFlow<State<Barber>> = MutableStateFlow(State.Loading())
    var salon: MutableStateFlow<State<SalonProfile>> = MutableStateFlow(State.Loading())


    fun getReservation(): StateFlow<State<Reservation>> {
        if (reservationFlow == null) {
            reservationFlow = MutableStateFlow(State.Loading())
            Firebase.database.reference.child(Keys.reservations).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val reservation =
                            snapshot.children.mapNotNull { it.getValue(Reservation::class.java) }.firstOrNull { it.client?.userId == clientProfile?.userId } ?: throw Exception("reservation not found")
                        initBarberData(reservation.barberId)
                        initSalon(reservation.salonId)
                        reservationFlow!!.value = State.Success(reservation)
                    } catch (e: Exception) {
                        reservationFlow!!.value = State.Error(e.message ?: e.localizedMessage)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    reservationFlow!!.value = State.Error(error.message)
                }
            })
        }

        return reservationFlow!!;

    }

    fun cancelReservation(reservationId: String) = flow {
        emit(State.Loading())
        try {
            Firebase.database.reference.child(reservations).child(reservationId).removeValue().await()
            emit(State.Success(Unit))
        } catch (e: Exception) {
            emit(State.Error(e.message ?: e.localizedMessage))
        }

    }

    fun initBarberData(barberId: String) = viewModelScope.launch {
        barber.value = State.Loading()
        try {
            val barberData = salonServices.getBarber(barberId)
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