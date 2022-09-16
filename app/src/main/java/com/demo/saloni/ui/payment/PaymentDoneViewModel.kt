package com.demo.saloni.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.saloni.data.local.CashedData
import com.demo.saloni.data.remote.ClientServices
import com.demo.saloni.data.remote.Keys
import com.demo.saloni.data.remote.SalonServices
import com.demo.saloni.data.remote.entities.Barber
import com.demo.saloni.data.remote.entities.Reservation
import com.demo.saloni.data.remote.entities.SalonProfile
import com.demo.saloni.ui.core.State
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.sql.RowId

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