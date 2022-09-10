package com.demo.saloni.ui.auth.signup

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.demo.saloni.data.remote.AuthServices
import com.demo.saloni.data.remote.entities.ClientProfile
import com.demo.saloni.data.remote.entities.SalonProfile
import com.demo.saloni.ui.core.State
import kotlinx.coroutines.flow.flow

class SignUpViewModel : ViewModel() {
    private var authService = AuthServices()
    var imageUri: Uri? = null;

    fun signUpClient(image: Uri?, name: String, civilId: String, dataOfBirth: String, phoneNumber: String, email: String, password: String) = flow {
        emit(State.Loading())
        try {
            val profile = authService.signUpClient(image, name, civilId, dataOfBirth, phoneNumber, email, password)
            emit(State.Success(profile));
        } catch (e: Throwable) {
            emit(State.Error(e.message ?: e.localizedMessage))
        }

    }

    fun signUpSalon(
        image: Uri?, name: String, phoneNumber: String, email: String,address:String, password: String, facebook: String, instagram: String, twitter: String
    ) = flow {
        emit(State.Loading())
        try {
            val profile = authService.signUpSalon(image, name, phoneNumber, email,address, password, facebook, instagram, twitter)
            emit(State.Success(profile));
        } catch (e: Throwable) {
            emit(State.Error(e.message ?: e.localizedMessage))
        }

    }
}