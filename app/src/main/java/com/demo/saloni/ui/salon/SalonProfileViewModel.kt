package com.demo.saloni.ui.salon

import androidx.lifecycle.ViewModel
import com.demo.saloni.data.local.CashedData
import com.demo.saloni.data.remote.entities.SalonProfile

class SalonProfileViewModel : ViewModel() {

    val salonProfile: SalonProfile
        get() {
            return CashedData.salonProfile!!
        }



}