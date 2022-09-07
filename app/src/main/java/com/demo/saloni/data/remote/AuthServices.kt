package com.demo.saloni.data.remote

import android.net.Uri
import android.util.Log
import com.demo.saloni.data.remote.Keys.client_profiles
import com.demo.saloni.data.remote.Keys.salons_profiles
import com.demo.saloni.data.remote.entities.ClientProfile
import com.demo.saloni.data.remote.entities.SalonProfile
import com.demo.saloni.data.remote.entities.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.function.Consumer
import kotlin.coroutines.suspendCoroutine

class AuthServices {

    fun signInClient(
        emailOrPhone: String,
        password: String,
        onSuccess: Consumer<ClientProfile>,
        onFailure: Consumer<String>,
    ) {
        Firebase.database.reference.child(client_profiles).get().addOnSuccessListener { data ->
            val u = data.children.map { it.getValue(ClientProfile::class.java) }.firstOrNull {
                it?.email?.uppercase() == emailOrPhone.uppercase() ||
                        it?.phoneNumber?.uppercase() == emailOrPhone.uppercase()
            }

            if (u == null) {
                onFailure.accept("user not found")
            } else {
                Firebase.auth.signInWithEmailAndPassword(u.email, password).addOnSuccessListener {
                    if(it.user==null)
                        onFailure.accept("user not found")

                    Firebase.database.reference.child(client_profiles).child(it.user!!.uid).get()
                        .addOnSuccessListener {
                            val profile = it.getValue(ClientProfile::class.java)
                            if (profile == null)
                                onFailure.accept("profile not found")
                            else
                                onSuccess.accept(profile)
                        }

                }
            }

        }.addOnFailureListener {
            onFailure.accept(it.message ?: it.localizedMessage ?: "")
        }
    }

    fun signInSalon(
        emailOrPhone: String,
        password: String,
        onSuccess: Consumer<SalonProfile>,
        onFailure: Consumer<String>,
    ) {
        Firebase.database.reference.child(salons_profiles).get().addOnSuccessListener { data ->
            val u = data.children.map { it.getValue(SalonProfile::class.java) }.firstOrNull {
                it?.email?.uppercase() == emailOrPhone.uppercase() ||
                        it?.phoneNumber?.uppercase() == emailOrPhone.uppercase()
            }

            if (u == null) {
                onFailure.accept("user not found")
            } else {
                Firebase.auth.signInWithEmailAndPassword(u.email, password).addOnSuccessListener {
                    if(it.user==null)
                        onFailure.accept("user not found")

                    Firebase.database.reference.child(salons_profiles).child(it.user!!.uid).get()
                        .addOnSuccessListener {
                            val profile = it.getValue(SalonProfile::class.java)
                            if (profile == null)
                                onFailure.accept("profile not found")
                            else
                                onSuccess.accept(profile)
                        }
                }
            }

        }.addOnFailureListener {
            onFailure.accept(it.message ?: it.localizedMessage ?: "")
        }
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
            downloadUri ="clientProfileImages/${image.lastPathSegment}"
        }

        val firebaseAuth = Firebase.auth.createUserWithEmailAndPassword(email, password).await()
        val clientProfile = ClientProfile(
            downloadUri, name, civilId, dataOfBirth, phoneNumber, email
        )

        if(firebaseAuth.user==null)
            throw Exception("user not found")

        Firebase.database.reference.child(client_profiles).child(firebaseAuth.user!!.uid).setValue(clientProfile)
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
            SalonProfile(downloadUri, name, phoneNumber, email, facebook, instagram, twitter)

        if (firebaseAuth.user == null)
            throw Exception("user not found")
        else
            Firebase.database.reference.child(salons_profiles).child(firebaseAuth.user!!.uid).setValue(salonProfile).await()

        Firebase.auth.signOut()
        return salonProfile;
    }


}

