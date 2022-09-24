package com.demo.saloni.ui.salonpreview

import androidx.lifecycle.ViewModel
import com.demo.saloni.data.local.CashedData
import com.demo.saloni.data.remote.SalonServices
import com.demo.saloni.ui.core.State
import kotlinx.coroutines.flow.flow

class SalonPreviewViewModel :ViewModel() {
    val salonServices = SalonServices.getInstance()

    fun getBarbers(salonId:String) =
            salonServices.getBarbers(salonId)


}