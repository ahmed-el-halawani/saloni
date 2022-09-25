package com.demo.barber.ui.homeclient

import androidx.lifecycle.ViewModel
import com.demo.barber.data.local.CashedData
import com.demo.barber.data.remote.ClientServices
import com.demo.barber.data.remote.entities.SalonProfile
import com.demo.barber.ui.core.State
import kotlinx.coroutines.flow.flow

class HomeClientViewModel : ViewModel() {
    val clientProfile = CashedData.clientProfile;
    val clientServices = ClientServices.getInstance()


    fun getSalons() = flow<State<List<SalonProfile>>> {
        emit(State.Loading())
        try {
            clientServices.getSalons().collect { emit(State.Success(it)) }
        } catch (e: Throwable) {
            emit(State.Error(e.message ?: e.localizedMessage))
        }
    }


}