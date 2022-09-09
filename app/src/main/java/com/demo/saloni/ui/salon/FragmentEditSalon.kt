package com.demo.saloni.ui.salon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.demo.saloni.R
import com.demo.saloni.databinding.FragmentEditSalonProfileBinding
import com.demo.saloni.databinding.FragmentSalonProfileBinding

class FragmentEditSalon : Fragment() {

    val binding by lazy {
        FragmentEditSalonProfileBinding.inflate(layoutInflater)
    }
    val vm:SalonProfileViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }


}