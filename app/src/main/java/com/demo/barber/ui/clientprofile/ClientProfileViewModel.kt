package com.demo.barber.ui.clientprofile

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.demo.barber.data.local.CashedData
import com.demo.barber.data.remote.ClientServices
import com.demo.barber.data.remote.entities.ClientProfile
import com.demo.barber.ui.core.State
import kotlinx.coroutines.flow.flow

class ClientProfileViewModel : ViewModel() {
    val profile = CashedData.clientProfile!!
    var imageUri: Uri? = null;
    val clientServices = ClientServices.getInstance()
    var dataOfBirth = MutableLiveData("")


    fun editClient(client: ClientProfile) = flow {
        emit(State.Loading())
        try {
            emit(State.Success(clientServices.editClient(profile.userId, imageUri, client)))
        } catch (e: Exception) {
            emit(State.Error(e.message ?: e.localizedMessage))
        }
    }
}