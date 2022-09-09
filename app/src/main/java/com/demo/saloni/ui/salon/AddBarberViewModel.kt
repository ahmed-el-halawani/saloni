package com.demo.saloni.ui.salon

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.demo.saloni.data.local.CashedData
import com.demo.saloni.data.remote.SalonServices
import com.demo.saloni.data.remote.entities.Barber
import com.demo.saloni.data.remote.entities.Days
import com.demo.saloni.data.remote.entities.Service
import com.demo.saloni.data.remote.entities.ServicesType
import com.demo.saloni.ui.core.State
import kotlinx.coroutines.flow.flow

class AddBarberViewModel : ViewModel() {
    val salonProfile = CashedData.salonProfile;
    val selectedServices = ArrayList<Service>()
    var imageUri: Uri? = null;

    val salonServices = SalonServices();


    fun addBarber(
        name: String,
        phone: String,
        civilId: String,
        image: Uri?,
        workingDays: List<Days> = emptyList(),
        services: List<Service> = emptyList(),
        shiftStartIn:String="",
        shiftEntIn:String=""
    ) = flow {
        emit(State.Loading())
        try {
            val res = salonServices.addBarber(name,phone,civilId,image,workingDays,services,shiftStartIn,shiftEntIn)
            emit(State.Success(res))
        }catch (e:Throwable){
            emit(State.Error(e.message?:e.localizedMessage))
        }
    }

}