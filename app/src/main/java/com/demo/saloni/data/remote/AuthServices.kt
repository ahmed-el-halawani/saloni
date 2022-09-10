package com.demo.saloni.data.remote

import android.net.Uri
import android.util.Log
import com.demo.saloni.data.local.CashedData
import com.demo.saloni.data.remote.Keys.profiles
import com.demo.saloni.data.remote.entities.ClientProfile
import com.demo.saloni.data.remote.entities.Profile
import com.demo.saloni.data.remote.entities.SalonProfile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

class AuthServices {
    private val cashedData = CashedData;

    suspend fun signInClient(
        emailOrPhone: String,
        password: String,
    ): ClientProfile {
        val clientProfiles = Firebase.database.reference.child(profiles).get().await()

        val profile = clientProfiles.children.map { it.getValue(Profile::class.java) }.firstOrNull {
            it?.email?.uppercase() == emailOrPhone.uppercase() || it?.phoneNumber?.uppercase() == emailOrPhone.uppercase()
        } ?: throw Exception("Client Not Found")


        val user = Firebase.auth.signInWithEmailAndPassword(profile.email, password).await().user ?: throw Exception("Client Not Found")
        val client = clientProfiles.child(user.uid).getValue(ClientProfile::class.java) ?: throw Exception("Client Not Found")
        cashedData.clientProfile = client
        return client
    }

    suspend fun signInSalon(
        emailOrPhone: String,
        password: String,
    ): SalonProfile {
        val salonProfiles = Firebase.database.reference.child(profiles).get().await()

        val profile = salonProfiles.children.map { it.getValue(Profile::class.java) }.firstOrNull {
            it?.email?.uppercase() == emailOrPhone.uppercase() || it?.phoneNumber?.uppercase() == emailOrPhone.uppercase()
        } ?: throw Exception("User Not Found")

        val user = Firebase.auth.signInWithEmailAndPassword(profile.email, password).await().user ?: throw Exception("User Not Found")
        val salonProfile = salonProfiles.child(user.uid).getValue(SalonProfile::class.java) ?: throw Exception("Client Not Found")
        cashedData.salonProfile = salonProfile;
        return salonProfile;
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

        val firebaseAuth = Firebase.auth.createUserWithEmailAndPassword(email, password).await()
        val clientProfile = ClientProfile(
            downloadUri, name, civilId, dataOfBirth, phoneNumber, email
        )

        if (firebaseAuth.user == null)
            throw Exception("user not found")

        Firebase.database.reference.child(profiles).child(firebaseAuth.user!!.uid).setValue(clientProfile)
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
        address:String,
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

        val firebaseAuth = Firebase.auth.createUserWithEmailAndPassword(email, password).await()

        val salonProfile =
            SalonProfile(downloadUri, name, phoneNumber, email,address, facebook, instagram, twitter)

        if (firebaseAuth.user == null)
            throw Exception("user not found")
        else
            Firebase.database.reference.child(profiles).child(firebaseAuth.user!!.uid).setValue(salonProfile).await()

        Firebase.auth.signOut()
        return salonProfile;
    }


}

