package com.demo.barber.ui.salon

import androidx.lifecycle.ViewModel
import com.demo.barber.data.local.CashedData
import com.demo.barber.data.remote.entities.SalonProfile

class SalonProfileViewModel : ViewModel() {

    val salonProfile: SalonProfile
        get() {
            return CashedData.salonProfile!!
        }



}