package com.demo.saloni.ui.homeslone

import androidx.lifecycle.ViewModel
import com.demo.saloni.data.local.CashedData

class HomeSalonViewModel :ViewModel() {
    val salonProfile = CashedData.salonProfile;

}