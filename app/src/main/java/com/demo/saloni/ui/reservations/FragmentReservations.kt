package com.demo.saloni.ui.reservations

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import com.bumptech.glide.Glide
import com.demo.saloni.R
import com.demo.saloni.data.local.CashedData
import com.demo.saloni.data.remote.entities.Barber
import com.demo.saloni.data.remote.entities.Reservation
import com.demo.saloni.data.remote.entities.Service
import com.demo.saloni.databinding.FragmentReservationsBinding
import com.demo.saloni.databinding.ItemBarberBinding
import com.demo.saloni.databinding.ItemReservationBinding
import com.demo.saloni.databinding.ItemServicesBinding
import com.demo.saloni.ui.core.BaseFragment
import com.demo.saloni.ui.core.State
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.newcore.easyrecyclergenerator.rvSingleList

class FragmentReservations : BaseFragment() {

    val vm: ReservationSalonViewModel by viewModels()

    val binding: FragmentReservationsBinding by lazy {
        FragmentReservationsBinding.inflate(layoutInflater)
    }

    val reservationAdapter by lazy {
        rvSingleList(binding.rcReservation, ItemReservationBinding::inflate, emptyList<Reservation>()) {
            listBuilder { itemReservationBinding, reservation ->
                if (reservation.client?.image != null) {
                    Glide.with(requireContext()).load(Firebase.storage.reference.child(reservation.client?.image!!)).into(itemReservationBinding.ivUserProfileImage)
                }

                rvSingleList(itemReservationBinding.rvServices, ItemServicesBinding::inflate, reservation.services) {
                    listBuilder { itemServicesBinding: ItemServicesBinding, service: Service ->
                        itemServicesBinding.tvServiceName.text = service.id.title
                    }
                }
            }
        }
    }

    val barbersAdapter by lazy {
        rvSingleList(binding.rcBarberList, ItemBarberBinding::inflate, emptyList<Barber>()) {
            listBuilder { itemBarberBinding, barber ->
                itemBarberBinding.tvBarberName.text = barber.name
                if (barber.image != null)
                    Glide.with(requireContext()).load(
                        Firebase.storage.reference.child(barber.image!!)
                    ).into(itemBarberBinding.ivBarberImage)

                itemBarberBinding.container.setOnClickListener {
                    vm.getReservations(barber.barberId).asLiveData().observe(viewLifecycleOwner) {
                        when (it) {
                            is State.Error -> Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                            is State.Loading -> Toast.makeText(context, "loading", Toast.LENGTH_SHORT).show()
                            is State.Success -> reservationAdapter.setList(it.data!!)
                        }
                    }
                }

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.getBarbers(CashedData.salonProfile!!.salonId).asLiveData().observe(viewLifecycleOwner) {
            hideMainLoading()
            when (it) {
                is State.Error -> Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                is State.Loading -> showMainLoading()
                is State.Success -> {
                    barbersAdapter.setList(it.data!!)
                }
            }
        }

    }
}