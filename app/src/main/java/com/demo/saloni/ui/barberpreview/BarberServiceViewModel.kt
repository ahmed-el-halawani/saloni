package com.demo.saloni.ui.barberpreview

import android.annotation.SuppressLint
import android.net.Uri
import android.view.View
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.demo.saloni.data.local.CashedData
import com.demo.saloni.data.remote.ClientServices
import com.demo.saloni.data.remote.SalonServices
import com.demo.saloni.data.remote.entities.*
import com.demo.saloni.ui.core.State
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.util.*
import kotlin.collections.ArrayList

class BarberServiceViewModel() : ViewModel() {
    val salonProfile = CashedData.salonProfile;
    val selectedServices = ArrayList<Service>()
    var selectedDay: Date? = null
    var selectedTime: Date? = null

    var date = Calendar.getInstance()
    var isCash: Boolean = true

    val calender = Calendar.getInstance()

    var currentSelectedItem: View? = null
    var currentSelectedTimeItem: View? = null

    val clientService = ClientServices.getInstance()


    fun addReservation(
        barberId: String = "",
        services: List<Service> = emptyList(),
        paymentMethod: PaymentMethods = PaymentMethods.Cash,
    ) = flow {
        emit(State.Loading())
        try {
            val res = clientService.addReservation(
                Reservation(
                    barberId, services, date.time, paymentMethod
                )
            );
            emit(State.Success(res))
        } catch (e: Throwable) {
            emit(State.Error(e.message ?: e.localizedMessage))
        }
    }

}