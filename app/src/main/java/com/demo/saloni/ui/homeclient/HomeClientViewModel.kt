package com.demo.saloni.ui.homeclient

import androidx.lifecycle.ViewModel
import com.demo.saloni.data.local.CashedData
import com.demo.saloni.data.remote.ClientServices
import com.demo.saloni.data.remote.entities.SalonProfile
import com.demo.saloni.ui.core.State
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