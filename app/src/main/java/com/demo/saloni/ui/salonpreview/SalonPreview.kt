package com.demo.saloni.ui.salonpreview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.demo.saloni.R
import com.demo.saloni.data.remote.entities.Barber
import com.demo.saloni.databinding.FragmentSalonPreviewBinding
import com.demo.saloni.databinding.ItemBarberBinding
import com.demo.saloni.ui.core.BaseFragment
import com.demo.saloni.ui.core.State
import com.demo.saloni.ui.homeslone.FragmentHomeSalonDirections
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.newcore.easyrecyclergenerator.rvSingleList

class SalonPreview : BaseFragment() {

    private val binding : FragmentSalonPreviewBinding by lazy {
        FragmentSalonPreviewBinding.inflate(layoutInflater)
    }

    private val args:SalonPreviewArgs by navArgs()

    private val salon by lazy { args.salon }

    private val vm:SalonPreviewViewModel by viewModels()

    private val barbersAdapter by lazy {
        rvSingleList(binding.rvBarbers, ItemBarberBinding::inflate, emptyList<Barber>(), layoutManager = GridLayoutManager(requireContext(), 4)) {
            listBuilder { itemBarberBinding, barber ->
                if (!barber.image.isNullOrBlank())
                    Glide.with(requireContext()).load(Firebase.storage.reference.child(barber.image!!)).into(itemBarberBinding.ivBarberImage)

                itemBarberBinding.tvBarberName.text = barber.name

                itemBarberBinding.container.setOnClickListener {
                    findNavController().navigate(
                        SalonPreviewDirections.actionSalonPreviewToFragmentBarberPreview(barber)
                    )
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        // Inflate the layout for this fragment
        setBackButton(binding.btnBack)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.getBarbers(salon.salonId).asLiveData().observe(viewLifecycleOwner) {
            hideSubLoading()
            when (it) {
                is State.Error -> Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                is State.Loading -> showSubLoading()
                is State.Success -> barbersAdapter.setList(it.data ?: emptyList())
            }
        }

        binding.apply {
            if (salon.image != null)
                Glide.with(requireContext()).load(Firebase.storage.reference.child(salon.image!!)).into(ivSalonProfileImage2)

            tvSalonName2.text = salon.name
            tvAddress.text = salon.address
            tvPhoneNumber.text = salon.phoneNumber
            tvEmail.text =salon.email

        }
    }



}