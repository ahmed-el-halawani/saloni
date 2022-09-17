package com.demo.saloni.data.remote

import android.net.Uri
import androidx.core.net.toFile
import com.demo.saloni.data.local.CashedData
import com.demo.saloni.data.remote.Keys.barber_child
import com.demo.saloni.data.remote.Keys.profiles
import com.demo.saloni.data.remote.Keys.reservations
import com.demo.saloni.data.remote.entities.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import java.util.*
import java.util.concurrent.Flow

class ClientServices {
    private val salonsFlow = MutableStateFlow(emptyList<SalonProfile>())
    private val salonFlow = MutableStateFlow<SalonProfile?>(null)


    private constructor()

    companion object {
        private var instance: ClientServices? = null
        const val LOCK = ""

        fun getInstance() = instance ?: synchronized(LOCK) { instance ?: ClientServices().also { instance = it } }
    }

    fun getSalons(): StateFlow<List<SalonProfile>> {
        Firebase.database.reference.child(profiles).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                salonsFlow.value = snapshot.children.mapNotNull {
                    val profile = it.getValue(Profile::class.java);
                    if (profile != null && profile.salon)
                        it.getValue(SalonProfile::class.java)
                    else
                        null
                }
            }

            override fun onCancelled(error: DatabaseError) {
                throw Exception(error.message)
            }
        })
        return salonsFlow;
    }


    suspend fun addReservation(
        reservation: Reservation
    ): String {
        val newReservation = Firebase.database.reference.child(reservations).push()
        reservation.reservationId = newReservation.key ?: UUID.randomUUID().toString();
        reservation.client = CashedData.clientProfile;
        newReservation.setValue(reservation).await()

        return reservation.reservationId;
    }


}