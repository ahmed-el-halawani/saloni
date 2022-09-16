package com.demo.saloni.ui.clientQr

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.demo.saloni.R
import com.demo.saloni.data.remote.entities.SalonProfile
import com.demo.saloni.databinding.FragmentClientQrBinding
import com.demo.saloni.databinding.FragmentHomeClientBinding
import com.demo.saloni.databinding.ItemSalonBinding
import com.demo.saloni.ui.core.BaseFragment
import com.demo.saloni.ui.core.State
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.newcore.easyrecyclergenerator.rvSingleList


class ClientQrFragment : BaseFragment() {
    val vm: ClientQrViewModel by viewModels()

    private val binding: FragmentClientQrBinding by lazy {
        FragmentClientQrBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.getReservation().asLiveData().observe(viewLifecycleOwner){

        }
    }


}