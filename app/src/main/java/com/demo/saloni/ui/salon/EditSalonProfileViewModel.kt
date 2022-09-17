package com.demo.saloni.ui.salon

import androidx.lifecycle.ViewModel
import com.demo.saloni.data.local.CashedData
import com.demo.saloni.data.remote.SalonServices

class EditSalonProfileViewModel :ViewModel() {
    val salonProfile = CashedData.salonProfile;
    val salonServices = SalonServices.getInstance()

}