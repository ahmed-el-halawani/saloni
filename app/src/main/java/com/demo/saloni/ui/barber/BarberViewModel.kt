package com.demo.saloni.ui.barber

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.demo.saloni.data.local.CashedData
import com.demo.saloni.data.remote.Keys
import com.demo.saloni.data.remote.SalonServices
import com.demo.saloni.data.remote.entities.*
import com.demo.saloni.ui.core.State
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

class BarberViewModel() : ViewModel() {
    val salonProfile = CashedData.salonProfile;
    val selectedServices = ArrayList<Service>()
    var imageUri: Uri? = null;
    var barber = MutableStateFlow<State<Barber>>(State.Loading())

    val salonServices = SalonServices.getInstance();


//    fun getBarber(barberId: String) = flow<State<Barber>> {
//        emit(State.Loading())
//        try {
//            salonServices.getBarberFlow(barberId).collect {
//                if (it == null)
//                    emit(State.Error("barber not found"))
//                else
//                    emit(State.Success(it))
//            }
//        } catch (e: Throwable) {
//            emit(State.Error(e.message ?: e.localizedMessage))
//        }
//    }


    fun getBarber(barberId: String): StateFlow<State<Barber>> {
        barber.value = State.Loading()
        Firebase.database.reference.child(Keys.barber_child).child(barberId) .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val barberData = snapshot.getValue(Barber::class.java)
                if (barberData != null) {
                    barber.value = State.Success(barberData)
                } else
                    barber.value = State.Error("barber not found")
            }

            override fun onCancelled(error: DatabaseError) {
                barber.value = State.Error("barber not found")
            }
        })

        return barber.asStateFlow()
    }


}