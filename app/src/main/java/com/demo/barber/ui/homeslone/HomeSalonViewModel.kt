package com.demo.barber.ui.homeslone

import androidx.lifecycle.ViewModel
import com.demo.barber.data.local.CashedData
import com.demo.barber.data.remote.SalonServices
import com.demo.barber.data.remote.entities.Barber
import com.demo.barber.data.remote.entities.SalonProfile
import com.demo.barber.ui.core.State
import kotlinx.coroutines.flow.MutableStateFlow

class HomeSalonViewModel : ViewModel() {
    val salonProfile: SalonProfile
        get() {
            return CashedData.salonProfile!!
        }

    val salonServices = SalonServices.getInstance()

    val barbersList = MutableStateFlow<State<List<Barber>>>(State.Loading(emptyList()))


    fun getBarbers(salonId: String) = salonServices.getBarbers(salonId)


}