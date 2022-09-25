package com.demo.barber.data.remote

import android.net.Uri
import com.demo.barber.data.local.CashedData
import com.demo.barber.data.remote.Keys.profiles
import com.demo.barber.data.remote.entities.ClientProfile
import com.demo.barber.data.remote.entities.Profile
import com.demo.barber.data.remote.entities.SalonProfile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthServices {
    private val cashedData = CashedData;

    suspend fun signIn(
        emailOrPhone: String,
        password: String,
    ): Profile {
        val clientProfiles = Firebase.database.reference.child(profiles).get().await()

        val profile = clientProfiles.children.map { it.getValue(Profile::class.java) }.firstOrNull {
            it?.email?.uppercase() == emailOrPhone.uppercase() || it?.phoneNumber?.uppercase() == emailOrPhone.uppercase()
        } ?: throw Exception("Client Not Found")


        val user = Firebase.auth.signInWithEmailAndPassword(profile.email, password).await().user ?: throw Exception("Client Not Found")

        return getProfile(user.uid) ?: throw Exception("profile not found")
    }



    suspend fun signUpClient(
        image: Uri?,
        name: String,
        civilId: String,
        dataOfBirth: String,
        phoneNumber: String,
        email: String,
        password: String,
    ): ClientProfile {
        var downloadUri: String? = null;
        if (image != null) {

            val mountainImagesRef =
                Firebase.storage.reference.child("clientProfileImages/${image.lastPathSegment}")
            mountainImagesRef.putFile(image).await()
            downloadUri = "clientProfileImages/${image.lastPathSegment}"
        }

        val user = Firebase.auth.createUserWithEmailAndPassword(email, password).await().user ?: throw Exception("user not found")


        val clientProfile = ClientProfile(
            user.uid, downloadUri, name, civilId, dataOfBirth, phoneNumber, email
        )
        Firebase.database.reference.child(profiles).child(user.uid).setValue(clientProfile)
            .await()

        Firebase.auth.signOut()
        return clientProfile;

    }


    suspend fun signUpSalon(
        image: Uri?,
        name: String,
        phoneNumber: String,
        email: String,
        password: String,
        address: String,
        facebook: String,
        instagram: String,
        twitter: String
    ): SalonProfile {
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

        val user = Firebase.auth.createUserWithEmailAndPassword(email, password).await().user ?: throw Exception("user not found")

        val salonProfile =
            SalonProfile(user.uid, downloadUri, name, phoneNumber, email, address, facebook, instagram, twitter)

        Firebase.database.reference.child(profiles).child(user.uid).setValue(salonProfile).await()

        Firebase.auth.signOut()
        return salonProfile;
    }


    suspend fun getProfile(userId: String) = withContext(Dispatchers.IO) {
        val z = Firebase.database.reference.child(profiles).child(userId).get().await() ?: throw Exception("profile not found")
        if (z.child("salon").getValue(Boolean::class.java) == true) {
            z.getValue(SalonProfile::class.java).also {
                CashedData.salonProfile = it
            }
        } else
            z.getValue(ClientProfile::class.java).also {
                CashedData.clientProfile = it
            }
    }

}

