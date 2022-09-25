package com.demo.barber.data.local

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.demo.barber.data.remote.entities.ClientProfile
import com.demo.barber.data.remote.entities.SalonProfile
import com.google.gson.Gson

object CashedData {
    var app: Application? = null

    private val shared by lazy {
        app?.getSharedPreferences("cashedData", MODE_PRIVATE)
    }

    var clientProfile: ClientProfile?
        set(value) {
            shared!!.edit()?.apply {
                putString("clientProfile", Gson().toJson(value).toString())
                apply()
            }
        }
        get() {
            return Gson().fromJson(shared!!.getString("clientProfile", "{}"), ClientProfile::class.java)
        }

    var salonProfile: SalonProfile?
        set(value) {
            shared!!.edit()?.apply {
                putString("salonProfile", Gson().toJson(value).toString())
                apply()
            }
        }
        get() {
            return Gson().fromJson(shared!!.getString("salonProfile", "{}"), SalonProfile::class.java)
        }

}

