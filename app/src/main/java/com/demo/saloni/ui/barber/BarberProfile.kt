package com.demo.saloni.ui.barber

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.demo.saloni.R
import com.demo.saloni.data.remote.entities.Barber
import com.demo.saloni.data.remote.entities.ServicesType
import com.demo.saloni.databinding.FragmentEditBarberProfileBinding
import com.demo.saloni.databinding.FragmentProfileBarberBinding
import com.demo.saloni.ui.core.BaseFragment
import com.demo.saloni.ui.core.State
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class BarberProfile : BaseFragment() {

    val binding: FragmentProfileBarberBinding by lazy {
        FragmentProfileBarberBinding.inflate(layoutInflater)
    }
    val args: BarberProfileArgs by navArgs()
    val vm: BarberViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setBackButton(binding.btnBack)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.getBarber(args.barberId).asLiveData().observe(viewLifecycleOwner) { state ->
            hideMainLoading()
            when (state) {
                is State.Error -> Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                is State.Loading -> showMainLoading()
                is State.Success -> binding.apply {
                    val barber = state.data!!;
                    initActions(barber)

                    if (!barber.image.isNullOrBlank()) {
                        Glide.with(requireContext()).load(
                            Firebase.storage.reference.child(barber.image!!)
                        ).into(ivBarberProfile)
                    }

                    tvName.text = barber.name
                    tvSalonName.text = vm.salonProfile?.name
                    tvPhone.text = barber.phone
                    tvCiviId.text = barber.civilId

                    cvHairCut.visibility = View.INVISIBLE
                    cvBeardCut.visibility = View.INVISIBLE
                    cvCleaning.visibility = View.INVISIBLE
                    cvColoring.visibility = View.INVISIBLE

                    barber.services.forEach {
                        when (it.id) {
                            ServicesType.Null -> {}
                            ServicesType.HairCut -> {
                                cvHairCut.isVisible = true
                                tvHairCutPrice.text = it.price.toString()
                            }
                            ServicesType.BeardCut -> {
                                cvBeardCut.isVisible = true
                                tvBeardCutPrice.text = it.price.toString()

                            }
                            ServicesType.Cleaning -> {
                                cvCleaning.isVisible = true
                                tvCleaningPrice.text = it.price.toString()

                            }
                            ServicesType.Coloring -> {
                                cvColoring.isVisible = true
                                tvColoringPrice.text = it.price.toString()

                            }
                        }
                    }
                }
            }
        }
    }

    private fun initActions(barber: Barber) {
        binding.apply {
            btnEditProfile.setOnClickListener {
                findNavController().navigate(BarberProfileDirections.actionBarberProfileToEditBarberProfile(barber))
            }

            btnReservationList.setOnClickListener { }
        }
    }

}