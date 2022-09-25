package com.demo.barber.ui.salonpreview

import androidx.lifecycle.ViewModel
import com.demo.barber.data.remote.SalonServices

class SalonPreviewViewModel :ViewModel() {
    val salonServices = SalonServices.getInstance()

    fun getBarbers(salonId:String) =
            salonServices.getBarbers(salonId)


}