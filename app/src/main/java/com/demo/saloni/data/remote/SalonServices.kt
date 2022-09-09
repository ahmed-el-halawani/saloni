package com.demo.saloni.data.remote

import android.net.Uri
import com.demo.saloni.data.remote.Keys.barber_child
import com.demo.saloni.data.remote.entities.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

class SalonServices {

    suspend fun addBarber(
        name: String,
        phone: String,
        civilId: String,
        image: Uri?,
        workingDays: List<Days> = emptyList(),
        services: List<Service> = emptyList(),
         shiftStartIn:String="",
         shiftEntIn:String=""
    ): Barber {
        var downloadUri: String? = null;
        try {
            if (image != null) {
                val mountainImagesRef =
                    Firebase.storage.reference.child("salonProfileImages/${image.lastPathSegment}")
                mountainImagesRef.putFile(image).await()

                downloadUri = "salonProfileImages/${image.lastPathSegment}"
            }
        } catch (e: Exception) {
        }

        val user = Firebase.auth.currentUser ?: throw Exception("you are not logged in")

        val barber = Barber(user.uid, name, phone, civilId, downloadUri, workingDays, services,shiftStartIn,shiftEntIn)

        Firebase.database.reference.child(barber_child).push().setValue(barber).await()
        return barber;
    }

}