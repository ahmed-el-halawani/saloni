package com.demo.saloni.ui.report

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.demo.saloni.R
import com.demo.saloni.data.local.CashedData
import com.demo.saloni.data.remote.entities.Barber
import com.demo.saloni.data.remote.entities.PaymentMethods
import com.demo.saloni.data.remote.entities.Reservation
import com.demo.saloni.data.remote.entities.Service
import com.demo.saloni.databinding.*
import com.demo.saloni.ui.core.BaseFragment
import com.demo.saloni.ui.core.State
import com.demo.saloni.ui.core.glide
import com.demo.saloni.ui.reservations.ReservationSalonViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.newcore.easyrecyclergenerator.rvSingleList
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentReport.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentReport : BaseFragment() {
    val vm: ReservationSalonViewModel by viewModels()

    val binding: FragmentReportBinding by lazy {
        FragmentReportBinding.inflate(layoutInflater)
    }

    val reservationAdapter by lazy {
        val dayNumberFormatter = SimpleDateFormat("dd MMMM,yyyy")
        val timeFormatter = SimpleDateFormat("h:mm a")

        rvSingleList(binding.rcReservation, ItemReservationBinding::inflate, emptyList<Reservation>()) {
            listBuilder { itemReservationBinding, reservation ->
                if (!reservation.client?.image.isNullOrBlank()) {
                    glide().load(Firebase.storage.reference.child(reservation.client?.image!!)).into(itemReservationBinding.ivUserProfileImage)
                }
                reservation.apply {
                    val date = Calendar.getInstance().apply {
                        time = reservation.date ?: Date()
                    }
                    itemReservationBinding.tvTotalPrice.text = services.sumOf { it.price }.toString()

                    itemReservationBinding.tvDate.text = dayNumberFormatter.format(date.time)
                    itemReservationBinding.tvTime.text = timeFormatter.format(date.time)
                    when (reservation.paymentMethod) {
                        PaymentMethods.Cash -> {
                            itemReservationBinding.paymentContainer.setBackgroundColor(resources.getColor(R.color.white))
                            itemReservationBinding.tvTotalPrice.setTextColor(resources.getColor(R.color.black))
                        }
                        PaymentMethods.Kent -> {
                            itemReservationBinding.tvCash.isVisible = false
                            itemReservationBinding.kent.isVisible = true
                        }
                    }


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
        rvSingleList(binding.rcBarberList, ItemBarberBinding::inflate, emptyList<Barber>(), layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)) {
            listBuilder { itemBarberBinding, barber ->
                itemBarberBinding.tvBarberName.text = barber.name
                if (!barber.image.isNullOrBlank())
                    glide().load(
                        Firebase.storage.reference.child(barber.image!!)
                    ).into(itemBarberBinding.ivBarberImage)

                itemBarberBinding.container.setOnClickListener {
                    vm.getReports(barber.barberId).asLiveData().observe(viewLifecycleOwner) {
                        hideMainLoading()
                        when (it) {
                            is State.Error -> Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                            is State.Loading -> showMainLoading()
                            is State.Success -> {
                                reservationAdapter.setList(it.data!!)
                                binding.tvTotalReport.text = it.data.sumOf { it.services.sumOf { it.price } }.toString()
                            }
                        }
                    }
                }

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setBackButton(binding.btnBack)
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