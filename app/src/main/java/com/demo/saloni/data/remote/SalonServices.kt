package com.demo.saloni.data.remote

import com.demo.saloni.data.local.CashedData
import com.demo.saloni.data.remote.Keys.profiles
import com.demo.saloni.data.remote.Keys.salons_profiles
import com.demo.saloni.data.remote.entities.ClientProfile
import com.demo.saloni.data.remote.entities.SalonProfile
import com.google.firebase.FirebaseException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SalonServices {

    suspend fun getSalonProfile(
        userId: String
    ): SalonProfile {
        val salonProfile = Firebase.database.reference.child(profiles).child(userId).get().await().getValue(SalonProfile::class.java) ?: throw Exception("User Not Found")
        CashedData.salonProfile = salonProfile;
        return salonProfile;
    }

    suspend fun getClientProfile(
        userId: String
    ): ClientProfile {
        val clientProfile = Firebase.database.reference.child(profiles).child(userId).get().await().getValue(ClientProfile::class.java) ?: throw Exception("User Not Found")
        CashedData.clientProfile = clientProfile;
        return clientProfile;
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