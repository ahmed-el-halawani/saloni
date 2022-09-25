package com.demo.barber.data.remote

import android.net.Uri
import com.demo.barber.data.local.CashedData
import com.demo.barber.data.remote.Keys.profiles
import com.demo.barber.data.remote.Keys.reservations
import com.demo.barber.data.remote.entities.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import java.util.*

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

    suspend fun editClient(
        clientId: String,
        image: Uri? = null,
        client: ClientProfile
    ): ClientProfile {
        var downloadUri: String? = null;
        try {
            if (image != null) {
                val mountainImagesRef =
                    Firebase.storage.reference.child("clientProfileImages/${image.lastPathSegment}")
                try {
                    mountainImagesRef.downloadUrl.await()
                } catch (e: StorageException) {
                    mountainImagesRef.putFile(image).await()
                }
                downloadUri = "clientProfileImages/${image.lastPathSegment}"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        val clientPath = Firebase.database.reference.child(profiles).child(clientId)
        val clientP = clientPath.get().await().getValue(ClientProfile::class.java)?.apply {
            this.name = client.name
            this.phoneNumber = client.phoneNumber
            this.dataOfBirth = client.dataOfBirth
            this.civilId = client.civilId
            downloadUri?.let { this.image = it }
            clientPath.setValue(this).await()
        } ?: throw Exception("client not found")

        CashedData.clientProfile = clientP
        return clientP;
    }

}