package com.demo.saloni.ui.homeslone

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.demo.saloni.R
import com.demo.saloni.databinding.FragmentHomeSalonBinding
import com.demo.saloni.ui.core.BaseFragment
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

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

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.apply {
//
//            Glide.with(requireContext()).load(Firebase.storage.reference
//                .child(vm.salonProfile?.salonImage ?: ""))
//                .into(binding.ivSalonProfileImage)
//
//            binding.tvSalonName.text = vm.salonProfile?.salonName
//
//
//        }
//
//
//        binding.btnProfile.setOnClickListener {
//            findNavController().navigate(
//                FragmentHomeSalonDirections.actionFragmentHomeSalonToSalonProfile()
//            )
//        }
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