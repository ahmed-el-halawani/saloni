package com.demo.saloni.ui.salon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.demo.saloni.databinding.FragmentSalonProfileBinding
import com.demo.saloni.ui.core.BaseFragment

class EditSalonProfile : BaseFragment() {

    val binding by lazy {
        FragmentSalonProfileBinding.inflate(layoutInflater)
    }
    val vm:SalonProfileViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}