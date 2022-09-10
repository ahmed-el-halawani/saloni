package com.demo.saloni.ui.barber

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.demo.saloni.data.local.CashedData
import com.demo.saloni.data.remote.SalonServices
import com.demo.saloni.data.remote.entities.*
import com.demo.saloni.ui.core.State
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class BarberViewModel() : ViewModel() {
    val salonProfile = CashedData.salonProfile;
    val selectedServices = ArrayList<Service>()
    var imageUri: Uri? = null;

    val salonServices = SalonServices.getInstance();


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


    fun editBarber(
        barberId: String,
        name: String,
        phone: String,
        civilId: String,
        image: Uri?,
        workingDays: List<Days> = emptyList(),
        services: List<Service> = emptyList(),
        shiftStartIn: ShiftTime = ShiftTime(),
        shiftEntIn: ShiftTime = ShiftTime()
    ) = flow {
        emit(State.Loading())
        try {
            val res = salonServices.editBarber(barberId,name, phone, civilId, image, workingDays, services, shiftStartIn, shiftEntIn)
            emit(State.Success(res))
        } catch (e: Throwable) {
            emit(State.Error(e.message ?: e.localizedMessage))
        }
    }


}