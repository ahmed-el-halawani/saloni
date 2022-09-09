package com.demo.saloni.ui.auth.splash

import androidx.lifecycle.ViewModel
import com.demo.saloni.data.local.CashedData
import com.demo.saloni.data.remote.AuthServices
import com.demo.saloni.ui.core.ILoadable
import com.demo.saloni.ui.core.Loadable
import com.demo.saloni.ui.core.State
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class SplashViewModel : ViewModel() {
    private val auth = AuthServices()


    fun getProfile(userId: String) = flow {
        emit(State.Loading())
        try {
            val res = auth.getProfile(userId)
            if (res == null)
                emit(State.Error("profile not found"))
            else
                emit(State.Success(res))
        } catch (e: Exception) {
            emit(State.Error(e.message ?: e.localizedMessage))
        }

    }


}