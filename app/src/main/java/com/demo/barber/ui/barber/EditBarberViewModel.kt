package com.demo.barber.ui.barber

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.demo.barber.data.local.CashedData
import com.demo.barber.data.remote.SalonServices
import com.demo.barber.data.remote.entities.*
import com.demo.barber.ui.core.State
import kotlinx.coroutines.flow.flow

class EditBarberViewModel(private val state: SavedStateHandle) : ViewModel() {
    val salonProfile = CashedData.salonProfile;
    val selectedServices = ArrayList<Service>()
    val selectedDays = ArrayList<Days>()

    var imageUri: Uri? = null;

    val salonServices = SalonServices.getInstance();

    init {
        val z= EditBarberProfileArgs.fromSavedStateHandle(state)
        selectedServices.clear()
        selectedServices.addAll(z.barber.services)

        selectedDays.clear()
        selectedDays.addAll(z.barber.workingDays)
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