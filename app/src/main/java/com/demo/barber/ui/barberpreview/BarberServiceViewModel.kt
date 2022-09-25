package com.demo.barber.ui.barberpreview

import android.view.View
import androidx.lifecycle.ViewModel
import com.demo.barber.data.local.CashedData
import com.demo.barber.data.remote.ClientServices
import com.demo.barber.data.remote.Keys
import com.demo.barber.data.remote.SalonServices
import com.demo.barber.data.remote.entities.*
import com.demo.barber.ui.core.State
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList

class BarberServiceViewModel() : ViewModel() {
    val selectedServices = ArrayList<Service>()
    var selectedDay: Date? = null
    var selectedTime: Date? = null

    var date = Calendar.getInstance()
    var isCash: Boolean = true

    val calender = Calendar.getInstance()

    var currentSelectedItem: View? = null
    var currentSelectedTimeItem: View? = null

    val clientService = ClientServices.getInstance()
    val salonServices = SalonServices.getInstance()

    fun isThereAnyReservation() = flow<State<Boolean>> {
        emit(State.Loading())
        try {
            val reservation = Firebase.database.reference.child(Keys.reservations).get().await()
            val clientReservation = reservation.children.mapNotNull { it.getValue(Reservation::class.java) }.any { it.client?.userId == CashedData.clientProfile?.userId }
            emit(State.Success(clientReservation))
        } catch (e: Exception) {
            emit(State.Error(e.message ?: e.localizedMessage))
        }
    }


    fun addReservation(
        barberId: String = "",
        salonId: String,
        services: List<Service> = emptyList(),
        paymentMethod: PaymentMethods = PaymentMethods.Cash,
    ) = flow {
        emit(State.Loading())
        try {
            val res = clientService.addReservation(
                Reservation(
                    barberId, salonId, services, date.time, paymentMethod
                )
            );
            emit(State.Success(res))
        } catch (e: Throwable) {
            emit(State.Error(e.message ?: e.localizedMessage))
        }
    }

}