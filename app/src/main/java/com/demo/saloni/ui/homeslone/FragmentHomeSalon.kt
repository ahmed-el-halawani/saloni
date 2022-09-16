package com.demo.saloni.ui.homeslone

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.demo.saloni.R
import com.demo.saloni.data.remote.entities.Barber
import com.demo.saloni.databinding.FragmentHomeSalonBinding
import com.demo.saloni.databinding.ItemBarberBinding
import com.demo.saloni.ui.core.BaseFragment
import com.demo.saloni.ui.core.State
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.newcore.easyrecyclergenerator.rvSingleList

class FragmentHomeSalon : BaseFragment() {

    val binding by lazy {
        FragmentHomeSalonBinding.inflate(layoutInflater)
    }

    val vm: HomeSalonViewModel by viewModels()

    val barbersAdapter by lazy {
        rvSingleList(binding.rvBarbers, ItemBarberBinding::inflate, emptyList<Barber>(), layoutManager = GridLayoutManager(requireContext(), 4)) {
            listBuilder { itemBarberBinding, barber ->
                if (!barber.image.isNullOrBlank())
                    Glide.with(requireContext()).load(Firebase.storage.reference.child(barber.image!!)).into(itemBarberBinding.ivBarberImage)

                itemBarberBinding.tvBarberName.text = barber.name

                itemBarberBinding.container.setOnClickListener {
                    findNavController().navigate(
                        FragmentHomeSalonDirections.actionFragmentHomeSalonToBarberProfile(barber.barberId)
                    )
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.apply {

            if (!vm.salonProfile?.image.isNullOrBlank())
                Glide.with(requireContext()).load(Firebase.storage.reference.child(vm.salonProfile?.image ?: "")).into(binding.ivSalonProfileImage2)

            tvSalonName2.text = vm.salonProfile?.name

        }.root
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initActions()

        vm.getBarbers(vm.salonProfile!!.salonId).asLiveData().observe(viewLifecycleOwner) {
            hideSubLoading()
            when (it) {
                is State.Error -> Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                is State.Loading -> showSubLoading()
                is State.Success -> barbersAdapter.setList(it.data ?: emptyList())
            }
        }


    }

    private fun initActions() {
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