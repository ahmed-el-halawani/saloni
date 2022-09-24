package com.demo.saloni.ui.reservations

import android.annotation.SuppressLint
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
import com.demo.saloni.databinding.FragmentReservationsBinding
import com.demo.saloni.databinding.ItemBarberBinding
import com.demo.saloni.databinding.ItemReservationBinding
import com.demo.saloni.databinding.ItemServicesBinding
import com.demo.saloni.ui.core.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.newcore.easyrecyclergenerator.rvSingleList
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
class FragmentReservations : BaseFragment() {

    val vm: ReservationSalonViewModel by viewModels()

    val binding: FragmentReservationsBinding by lazy {
        FragmentReservationsBinding.inflate(layoutInflater)
    }

    val reservationAdapter by lazy {
        val dayNumberFormatter = SimpleDateFormat("dd MMMM,yyyy")
        val timeFormatter = SimpleDateFormat("h:mm a")

        rvSingleList(binding.rcReservation, ItemReservationBinding::inflate, emptyList<Reservation>()) {
            listBuilder { itemReservationBinding, reservation ->

                reservation.apply {
                    if (!reservation.client?.image.isNullOrBlank()) {
                        firebaseGlide(reservation.client?.image!!, itemReservationBinding.ivUserProfileImage)
                    }

                    itemReservationBinding.tvUsername.text = this.client?.name
                    itemReservationBinding.tvPhoneNumber.text = this.client?.phoneNumber

                    val date = Calendar.getInstance().apply {
                        time = reservation.date ?: Date()
                    }
                    itemReservationBinding.tvTotalPrice.text = services.sumOf { it.price }.toMoney()

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
                    vm.getReservations(barber.barberId).asLiveData().observe(viewLifecycleOwner) {
                        hideMainLoading()
                        when (it) {
                            is State.Error -> Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                            is State.Loading -> showMainLoading()
                            is State.Success -> {
                                reservationAdapter.setList(it.data!!)
                                initDays()
                                initDateView()
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

        changeWeek()
        initDays()
        initDateView()
    }

    private fun changeWeek() {
        binding.apply {
            btnNextMonth.setOnClickListener {
                vm.calender.add(Calendar.WEEK_OF_YEAR, 1)
                initDays()
            }

            btnPrevMonth.setOnClickListener {
                vm.calender.add(Calendar.WEEK_OF_YEAR, -1)
                initDays()
            }


        }
    }

    fun initDays() {
        val currentWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)
        val currentSelectedWeek = vm.calender.get(Calendar.WEEK_OF_YEAR);
        binding.tvMonthYear.text =
            if (currentSelectedWeek == currentWeek)
                "Current Week"
            else if (currentSelectedWeek > currentWeek)
                "Next ${currentSelectedWeek - currentWeek} Week"
            else
                "Last ${currentWeek - currentSelectedWeek} Week"


        reservationAdapter.filter {
            val calender = Calendar.getInstance().apply { time = it.date }
            val resDay = calender.get(Calendar.WEEK_OF_YEAR)
            initDateView()
            resDay == currentSelectedWeek
        }
    }

    val dayMonthFormatter = SimpleDateFormat("dd MMM")

    fun initDateView() {
        val selectedTimeView = Calendar.getInstance().apply { time = vm.calender.time }
        selectedTimeView.set(Calendar.DAY_OF_WEEK, 1)
        val startDate = selectedTimeView.time
        selectedTimeView.set(Calendar.DAY_OF_WEEK, 1)
        val endDate = selectedTimeView.time
        binding.tvFrom.text = dayMonthFormatter.format(startDate)
        binding.tvTo.text = dayMonthFormatter.format(endDate)
    }
}