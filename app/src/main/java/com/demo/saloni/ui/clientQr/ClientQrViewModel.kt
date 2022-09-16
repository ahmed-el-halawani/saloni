package com.demo.saloni.ui.clientQr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.saloni.data.local.CashedData
import com.demo.saloni.data.remote.ClientServices
import com.demo.saloni.data.remote.Keys
import com.demo.saloni.data.remote.entities.Reservation
import com.demo.saloni.data.remote.entities.SalonProfile
import com.demo.saloni.ui.core.State
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ClientQrViewModel : ViewModel() {
    val clientProfile = CashedData.clientProfile;
    val clientServices = ClientServices.getInstance()
    var reservationFlow: MutableStateFlow<State<Reservation>>? = null


    fun getReservation(): StateFlow<State<Reservation>> {
        if (reservationFlow == null) {
            reservationFlow = MutableStateFlow(State.Loading())
            Firebase.database.reference.child(Keys.reservations).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val reservation =
                            snapshot.children.mapNotNull { it.getValue(Reservation::class.java) }.firstOrNull { it.client?.userId == clientProfile?.userId } ?: throw Exception("reservation not found")
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

}