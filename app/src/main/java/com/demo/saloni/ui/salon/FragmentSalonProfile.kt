package com.demo.saloni.ui.salon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.demo.saloni.R
import com.demo.saloni.databinding.ActivityMainBinding.inflate
import com.demo.saloni.databinding.FragmentHomeSalonBinding
import com.demo.saloni.databinding.FragmentSalonProfileBinding
import com.demo.saloni.ui.BaseFragment
import com.demo.saloni.ui.homeslone.FragmentHomeSalonDirections

class FragmentSalonProfile : BaseFragment() {

    val binding by lazy {
        FragmentSalonProfileBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnEditSalon.setOnClickListener {
            findNavController().navigate(
                FragmentSalonProfileDirections.actionFragmentSalonProfile2ToFragmentEditSalon()
            )
        }
    }

}