package com.demo.barber.ui.salon

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.demo.barber.data.local.CashedData
import com.demo.barber.data.remote.SalonServices
import com.demo.barber.data.remote.entities.SalonProfile
import com.demo.barber.ui.core.State
import kotlinx.coroutines.flow.flow

class EditSalonProfileViewModel : ViewModel() {
    val salonProfile = CashedData.salonProfile;
    val salonServices = SalonServices.getInstance()
    var imageUri: Uri? = null;


    fun editSalon(salon: SalonProfile) = flow<State<SalonProfile>> {
        emit(State.Loading())
        try {
            val salon = salonServices.editSalon(salonProfile!!.salonId, imageUri, salon)
            emit(State.Success(salon))
        } catch (e: Exception) {
            emit(State.Error(e.message ?: e.localizedMessage))
        }
    }


}