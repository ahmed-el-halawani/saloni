package com.demo.saloni.ui.clientprofile

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.demo.saloni.data.local.CashedData
import com.demo.saloni.data.remote.ClientServices
import com.demo.saloni.data.remote.entities.ClientProfile
import com.demo.saloni.ui.core.State
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