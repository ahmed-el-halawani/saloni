package com.demo.saloni.ui.barberpreview

import android.net.Uri
import android.view.View
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.demo.saloni.data.local.CashedData
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
    var selectedDay:Date?=null
    var selectedTime:Date?=null
    val isCash:Boolean=true
    var imageUri: Uri? = null;

    val calender = Calendar.getInstance()

    val salonServices = SalonServices.getInstance();

    var currentSelectedItem: View? = null
    var currentSelectedTimeItem: View? = null

    fun getBarber(barberId: String) = flow<State<Barber>> {
        emit(State.Loading())
        try {
            salonServices.getBarber(barberId).collect {
                if (it == null)
                    emit(State.Error("barber not found"))
                else
                    emit(State.Success(it))
            }
        } catch (e: Throwable) {
            emit(State.Error(e.message ?: e.localizedMessage))
        }
    }


}