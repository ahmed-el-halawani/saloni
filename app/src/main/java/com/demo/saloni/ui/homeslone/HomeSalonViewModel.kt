package com.demo.saloni.ui.homeslone

import androidx.lifecycle.ViewModel
import com.demo.saloni.data.local.CashedData
import com.demo.saloni.data.remote.SalonServices
import com.demo.saloni.data.remote.entities.Barber
import com.demo.saloni.data.remote.entities.SalonProfile
import com.demo.saloni.ui.core.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.Flow

class HomeSalonViewModel : ViewModel() {
    val salonProfile: SalonProfile
        get() {
            return CashedData.salonProfile!!
        }

    val salonServices = SalonServices.getInstance()

    val barbersList = MutableStateFlow<State<List<Barber>>>(State.Loading(emptyList()))


    fun getBarbers(salonId: String) = salonServices.getBarbers(salonId)


}