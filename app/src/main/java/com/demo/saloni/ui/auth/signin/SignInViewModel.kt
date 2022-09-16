package com.demo.saloni.ui.auth.signin

import androidx.lifecycle.ViewModel
import com.demo.saloni.data.local.CashedData
import com.demo.saloni.data.remote.AuthServices
import com.demo.saloni.ui.core.ILoadable
import com.demo.saloni.ui.core.Loadable
import com.demo.saloni.ui.core.State
import kotlinx.coroutines.flow.flow

class SignInViewModel : ViewModel() {
    private val auth = AuthServices()

    fun signIn(emailOrPhone: String, password: String) = flow {
        emit(State.Loading())
        try {
            val response = auth.signIn(emailOrPhone, password)
            emit(State.Success(response))
        } catch (e: Throwable) {
            emit(State.Error(message = e.message ?: e.localizedMessage))
        }
    }

}