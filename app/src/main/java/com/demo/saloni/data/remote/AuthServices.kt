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
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.flow
import java.util.function.Consumer

class AuthServices {

    fun signInClient(
        emailOrPhone: String,
        password: String,
        onSuccess: Consumer<ClientProfile>,
        onFailure: Consumer<String>,
    ) {
        Firebase.database.reference.child("users").get().addOnSuccessListener { data ->
            val u = data.children.map { it.getValue(User::class.java) }.firstOrNull {
                it?.email?.uppercase() == emailOrPhone.uppercase() ||
                        it?.phoneNumber?.uppercase() == emailOrPhone.uppercase()
            }

            if (u == null) {
                onFailure.accept("user not found")
            } else {
                Firebase.auth.signInWithEmailAndPassword(u.email, password).addOnSuccessListener {
                    Firebase.database.reference.child("clients_profiles").child(u.email).get()
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
        Firebase.database.reference.child("users").get().addOnSuccessListener { data ->
            val u = data.children.map { it.getValue(User::class.java) }.firstOrNull {
                it?.email?.uppercase() == emailOrPhone.uppercase() ||
                        it?.phoneNumber?.uppercase() == emailOrPhone.uppercase()
            }

            if (u == null) {
                onFailure.accept("user not found")
            } else {
                Firebase.auth.signInWithEmailAndPassword(u.email, password).addOnSuccessListener {
                    Firebase.database.reference.child(salons_profiles).child(u.email).get()
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


    fun signUpClient(
        image: Uri?,
        name: String,
        civilId: String,
        dataOfBirth: String,
        phoneNumber: String,
        email: String,
        password: String,
        onSuccess: Consumer<ClientProfile>,
        onFailure: Consumer<String>,

        ) {

        if(image == null)
        {
            val clientProfile = ClientProfile(
                image,name, civilId, dataOfBirth, phoneNumber, email
            );
            Firebase.database.reference.child(client_profiles).child(email)
                .setValue(clientProfile)
                .addOnSuccessListener { onSuccess.accept(clientProfile) }
                .addOnFailureListener {
                    onFailure.accept(
                        it.message ?: it.localizedMessage
                    )
                }
            return ;
        }
        val mountainImagesRef =
            FirebaseHelpers.storageRef.child("clientProfileImages/${image.lastPathSegment}")
        mountainImagesRef.putFile(image).addOnCompleteListener { imageTask ->
            Firebase.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                mountainImagesRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val clientProfile = ClientProfile(
                        downloadUri.path ?: "",
                        name, civilId, dataOfBirth, phoneNumber, email
                    );
                    Firebase.database.reference.child(client_profiles).child(email)
                        .setValue(clientProfile)
                        .addOnSuccessListener { onSuccess.accept(clientProfile) }
                        .addOnFailureListener {
                            onFailure.accept(
                                it.message ?: it.localizedMessage
                            )
                        }
                }
            }.addOnFailureListener {
                onFailure.accept(it.message ?: it.localizedMessage)
            }
        }.addOnFailureListener {
            onFailure.accept(it.message ?: it.localizedMessage)
        }
    }


    fun signUpSalon(
        image: Uri?,
        name: String,
        phoneNumber: String,
        email: String,
        facebook: String,
        instagram: String,
        twitter: String,
        password: String,
        onSuccess: Consumer<SalonProfile>,
        onFailure: Consumer<String>,

        ) {
        if (image == null) {
            val salonProfile = SalonProfile(
                image, name, phoneNumber, email, facebook, instagram, twitter
            );
            Firebase.database.reference.child(salons_profiles).child(email)
                .setValue(salonProfile)
                .addOnSuccessListener { onSuccess.accept(salonProfile) }
                .addOnFailureListener {
                    onFailure.accept(
                        it.message ?: it.localizedMessage
                    )
                }
            return;
        }

        val mountainImagesRef =
            FirebaseHelpers.storageRef.child("salonProfileImages/${image.lastPathSegment}")
        mountainImagesRef.putFile(image).addOnCompleteListener { imageTask ->
            Firebase.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                mountainImagesRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val salonProfile = SalonProfile(
                        downloadUri.path ?: "",
                        name, phoneNumber, email, facebook, instagram, twitter
                    );
                    Firebase.database.reference.child(salons_profiles).child(email)
                        .setValue(salonProfile)
                        .addOnSuccessListener { onSuccess.accept(salonProfile) }
                        .addOnFailureListener {
                            onFailure.accept(
                                it.message ?: it.localizedMessage
                            )
                        }
                }
            }.addOnFailureListener {
                onFailure.accept(it.message ?: it.localizedMessage)
            }
        }.addOnFailureListener {
            onFailure.accept(it.message ?: it.localizedMessage)
        }
    }


}

