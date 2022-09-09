package com.demo.saloni.ui.homeslone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.demo.saloni.R
import com.demo.saloni.databinding.FragmentHomeSalonBinding
import com.demo.saloni.ui.BaseFragment
import com.demo.saloni.ui.auth.signin.SignInFragmentDirections

class FragmentHomeSalon : BaseFragment() {

    val binding by lazy {
        FragmentHomeSalonBinding.inflate(layoutInflater)
    }

    val vm: HomeSalonViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSalonProfile.setOnClickListener {
            findNavController().navigate(
                FragmentHomeSalonDirections.actionFragmentHomeSalonToFragmentSalonProfile2()
            )
        }
        binding.btnAddBarber.setOnClickListener {
            findNavController().navigate(
                FragmentHomeSalonDirections.actionFragmentHomeSalonToFragmentAddBarber()
            )
        }

        binding.btnReservation.setOnClickListener {
            findNavController().navigate(
                FragmentHomeSalonDirections.actionFragmentHomeSalonToFragmentReservations()
            )
        }

        binding.btnReport.setOnClickListener {
            findNavController().navigate(
                FragmentHomeSalonDirections.actionFragmentHomeSalonToFragmentReport()
            )
        }


    }

}