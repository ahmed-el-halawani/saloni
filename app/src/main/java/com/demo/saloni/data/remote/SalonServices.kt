package com.demo.saloni.data.remote

import android.net.Uri
import androidx.core.net.toFile
import com.demo.saloni.data.remote.Keys.barber_child
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
import java.util.concurrent.Flow

class SalonServices {
    private val barbersFlow = MutableStateFlow(emptyList<Barber>())
    private val barberFlow = MutableStateFlow<Barber?>(null)

    private val barbersPath = Firebase.database.reference.child(barber_child)
    private var barberPath: DatabaseReference? = null

    private constructor()

    companion object {
        private var instance: SalonServices? = null
        const val LOCK = ""

        fun getInstance() = instance ?: synchronized(LOCK) { instance ?: SalonServices().also { instance = it } }
    }

    suspend fun addBarber(
        name: String,
        phone: String,
        civilId: String,
        image: Uri?,
        workingDays: List<Days> = emptyList(),
        services: List<Service> = emptyList(),
        shiftStartIn: ShiftTime = ShiftTime(),
        shiftEntIn: ShiftTime = ShiftTime(),
    ): Barber {
        var downloadUri: String? = null;
        try {
            if (image != null) {
                val mountainImagesRef =
                    Firebase.storage.reference.child("salonProfileImages/${image.lastPathSegment}")
                try {
                    mountainImagesRef.downloadUrl.await()
                } catch (e: StorageException) {
                    e.printStackTrace()
                    mountainImagesRef.putFile(image).await()
                }

                downloadUri = "salonProfileImages/${image.lastPathSegment}"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val user = Firebase.auth.currentUser ?: throw Exception("you are not logged in")
        val newBarberChild = Firebase.database.reference.child(barber_child).push()
        val barber = Barber(newBarberChild.key.toString(), user.uid, name, phone, civilId, downloadUri, workingDays, services, shiftStartIn, shiftEntIn)
        newBarberChild.setValue(barber).await()
        return barber;
    }

    suspend fun editBarber(
        barberId: String,
        name: String? = null,
        phone: String? = null,
        civilId: String? = null,
        image: Uri? = null,
        workingDays: List<Days> = emptyList(),
        services: List<Service> = emptyList(),
        shiftStartIn: ShiftTime? = null,
        shiftEntIn: ShiftTime? = null,
    ): Barber {
        var downloadUri: String? = null;
        try {
            if (image != null) {
                val mountainImagesRef =
                    Firebase.storage.reference.child("salonProfileImages/${image.lastPathSegment}")
                try {
                    mountainImagesRef.downloadUrl.await()
                } catch (e: StorageException) {
                    mountainImagesRef.putFile(image).await()
                }
                downloadUri = "salonProfileImages/${image.lastPathSegment}"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val barberPath = Firebase.database.reference.child(barber_child).child(barberId)
        val barber = barberPath.get().await().getValue(Barber::class.java)?.apply {
            update(name, phone, civilId, downloadUri, workingDays, services, shiftStartIn, shiftEntIn)
            barberPath.setValue(this).await()
        } ?: throw Exception("barber not found")

        return barber;
    }

    fun getBarbers(salonId: String): StateFlow<List<Barber>> {
        Firebase.database.reference.child(barber_child).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                barbersFlow.value = snapshot.children.mapNotNull {
                    val barber = it.getValue(Barber::class.java)
                    if (barber != null && barber.salonId == salonId)
                        barber
                    else
                        null
                }
            }

            override fun onCancelled(error: DatabaseError) {
                throw Exception(error.message)
            }
        })
        return barbersFlow;
    }

    private val barberEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            barberFlow.value = snapshot.getValue(Barber::class.java) ?: throw Exception("barber not found")
        }

        override fun onCancelled(error: DatabaseError) {
            throw Exception(error.message)
        }
    }

    fun getBarber(barberId: String): StateFlow<Barber?> {
        barberPath?.removeEventListener(barberEventListener)
        barberPath = barbersPath.child(barberId)
        barberPath?.addValueEventListener(barberEventListener)
        return barberFlow;
    }

}