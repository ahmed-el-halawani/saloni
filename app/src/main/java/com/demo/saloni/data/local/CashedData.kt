package com.demo.saloni.data.local

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.demo.saloni.data.remote.entities.ClientProfile
import com.demo.saloni.data.remote.entities.SalonProfile
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

