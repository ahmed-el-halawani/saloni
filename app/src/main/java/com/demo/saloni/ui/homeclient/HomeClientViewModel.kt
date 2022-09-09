package com.demo.saloni.ui.homeclient

import androidx.lifecycle.ViewModel
import com.demo.saloni.data.local.CashedData

class HomeClientViewModel :ViewModel() {
    val clientProfile = CashedData.clientProfile;

}