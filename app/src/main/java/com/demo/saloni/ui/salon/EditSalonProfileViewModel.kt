package com.demo.saloni.ui.salon

import androidx.lifecycle.ViewModel
import com.demo.saloni.data.local.CashedData

class EditSalonProfileViewModel :ViewModel() {
    val salonProfile = CashedData.salonProfile;
}