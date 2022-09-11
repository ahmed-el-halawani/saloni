package com.demo.saloni.ui.homeclient

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
import com.demo.saloni.databinding.FragmentHomeClientBinding
import com.demo.saloni.databinding.ItemSalonBinding
import com.demo.saloni.ui.core.BaseFragment
import com.demo.saloni.ui.core.State
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.newcore.easyrecyclergenerator.rvSingleList


class FragmentHomeClient : BaseFragment() {
    val vm: HomeClientViewModel by viewModels()

    private val binding: FragmentHomeClientBinding by lazy {
        FragmentHomeClientBinding.inflate(layoutInflater)
    }

    private val slaonAdapter by lazy {
        rvSingleList(binding.rcSalon, ItemSalonBinding::inflate, emptyList<SalonProfile>()) {
            listBuilder { itemSalonBinding, salonProfile ->
                itemSalonBinding.apply {
                    if (salonProfile.image != null)
                        Glide.with(requireContext()).load(Firebase.storage.reference.child(salonProfile.image)).into(ivSalonImage)

                    tvSalonName.text = salonProfile.name
                    tvSalonAddress.text = salonProfile.address

                    container.setOnClickListener {
                        findNavController().navigate(
                            FragmentHomeClientDirections.actionFragmentHomeClientToSalonPreview(salonProfile)
                        )
                    }

                }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val client = vm.clientProfile!!

        binding.apply {
            if (client.image != null)
                Glide.with(requireContext()).load(Firebase.storage.reference.child(client.image!!)).into(civClient)

            tvUsername.text = client.name

        }

        vm.getSalons().asLiveData().observe(viewLifecycleOwner) {
            hideMainLoading()
            when (it) {
                is State.Error -> Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                is State.Loading -> showMainLoading()
                is State.Success -> slaonAdapter.setList(it.data?: emptyList())
            }
        }


    }

}