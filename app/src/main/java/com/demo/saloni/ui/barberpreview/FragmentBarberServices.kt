package com.demo.saloni.ui.barberpreview

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.util.toRange
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.demo.saloni.R
import com.demo.saloni.data.remote.entities.PaymentMethods
import com.demo.saloni.data.remote.entities.Reservation
import com.demo.saloni.data.remote.entities.Service
import com.demo.saloni.data.remote.entities.ServicesType
import com.demo.saloni.databinding.FragmentBarberServicesBinding
import com.demo.saloni.databinding.FragmentProfileBarberBinding
import com.demo.saloni.databinding.ItemCalenderBinding
import com.demo.saloni.databinding.ItemTimeBinding
import com.demo.saloni.ui.core.BaseFragment
import com.demo.saloni.ui.core.State
import com.demo.saloni.ui.core.glide
import com.demo.saloni.ui.core.toMoney
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.newcore.easyrecyclergenerator.rvList
import com.newcore.easyrecyclergenerator.rvSingleList
import java.text.SimpleDateFormat
import java.time.Month
import java.util.*
import kotlin.math.log

@SuppressLint("SimpleDateFormat")
class FragmentBarberServices : BaseFragment() {

    val binding: FragmentBarberServicesBinding by lazy {
        FragmentBarberServicesBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setBackButton(binding.btnBack)
        return binding.root
    }

    val vm: BarberServiceViewModel by viewModels()

    val args: FragmentBarberServicesArgs by navArgs()

    val barber by lazy {
        args.barber
    }

    val salon by lazy {
        args.salon
    }


    val daysAdapter by lazy {
        rvSingleList(binding.rvDays, ItemCalenderBinding::inflate, emptyList<Date>(), layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)) {

            val dayNumberFormatter = SimpleDateFormat("dd")
            val dayNameFormatter = SimpleDateFormat("EEE")
            listBuilder { itemCalenderBinding, date ->
                itemCalenderBinding.civCategory.text = dayNameFormatter.format(date)
                itemCalenderBinding.tvCategoryName.text = dayNumberFormatter.format(date)

                if (vm.selectedDay != null && vm.selectedDay!!.date == date.date)
                    itemCalenderBinding.container.setBackgroundColor(resources.getColor(R.color.orange))
                else
                    itemCalenderBinding.container.setBackgroundColor(resources.getColor(android.R.color.transparent))


                if (barber.workingDays.any { it.dayName.uppercase() == dayNameFormatter.format(date).uppercase() }) {
                    itemCalenderBinding.container.setOnClickListener {
                        if (vm.selectedDay == null || vm.selectedDay != date) {
                            vm.currentSelectedItem?.setBackgroundColor(resources.getColor(android.R.color.transparent))
                            it.setBackgroundColor(resources.getColor(R.color.orange))
                            vm.selectedDay = date
                            vm.date.apply { time = date }
                            vm.currentSelectedItem = it
                        }
                    }
                } else {
                    itemCalenderBinding.container.setBackgroundColor(
                        resources.getColor(R.color.notActiveColor)
                    )
                }

            }
        }
    }

    val timeAdapter by lazy {
        val timeFormatter = SimpleDateFormat("h:mm a")

        val month = 1;
        val year = 2000;
        val day = 1

        val startShift = Calendar.getInstance().apply {
            set(Calendar.MONTH, month)
            set(Calendar.YEAR, year)
            set(Calendar.HOUR, args.barber.shiftStartIn!!.hour.toInt())
            set(Calendar.MINUTE, args.barber.shiftStartIn!!.minut.toInt())
            set(Calendar.AM_PM, if (args.barber.shiftStartIn!!.amOrPm == "AM") Calendar.AM else Calendar.PM)
        }


        val list = ArrayList<String>()

        val endShiftMode = if (args.barber.shiftEntIn!!.amOrPm == "AM") Calendar.AM else Calendar.PM;


        for (i in 0..24) {
            if (startShift.get(Calendar.HOUR) == args.barber.shiftEntIn!!.hour.toInt() && startShift.get(Calendar.AM_PM) == endShiftMode) {
                break;
            }
            val time = startShift.apply {
                add(Calendar.HOUR, 1)
            }
            list.add(timeFormatter.format(time.time))

        }
        rvSingleList(binding.rvTime, ItemTimeBinding::inflate, list.toList(), LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)) {
            listBuilder { binding, date ->
                binding.tvTime.text = date
                if (vm.selectedTime == timeFormatter.parse(date))
                    binding.tvTime.setBackgroundColor(resources.getColor(R.color.orange))
                else
                    binding.tvTime.setBackgroundColor(resources.getColor(android.R.color.transparent))

                binding.tvTime.setOnClickListener {
                    vm.currentSelectedTimeItem?.setBackgroundColor(resources.getColor(android.R.color.transparent))
                    binding.tvTime.setBackgroundColor(resources.getColor(R.color.orange))
                    vm.currentSelectedTimeItem = it

                    val calenderFromDate = Calendar.getInstance().apply {
                        time = timeFormatter.parse(date) ?: Date()
                    }
                    vm.date.apply {
                        set(Calendar.HOUR, calenderFromDate.get(Calendar.HOUR))
                        set(Calendar.MINUTE, calenderFromDate.get(Calendar.MINUTE))
                        set(Calendar.AM_PM, calenderFromDate.get(Calendar.AM_PM))
                    }

                    vm.selectedTime = timeFormatter.parse(date);
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (barber.model3DFirstStyle != null) {
            binding.ivHairCut3d1.setOnClickListener {
                findNavController().navigate(FragmentBarberServicesDirections.actionFragmentBarberServicesToModel3dViewer(barber.model3DFirstStyle!!.link!!))
            }

            if (barber.model3DFirstStyle!!.image!!.isNotBlank())
                glide().load(barber.model3DFirstStyle!!.image).into(binding.ivHairCut3d1)
        } else {
            binding.ivHairCut3d1.visibility = View.INVISIBLE
        }
        if (barber.model3DSecondStyle != null) {
            binding.ivHairCut3d2.setOnClickListener {
                findNavController().navigate(FragmentBarberServicesDirections.actionFragmentBarberServicesToModel3dViewer(barber.model3DSecondStyle!!.link!!))
            }

            if (barber.model3DSecondStyle!!.image!!.isNotBlank())
                glide().load(barber.model3DSecondStyle!!.image).into(binding.ivHairCut3d2)

        } else {
            binding.ivHairCut3d2.visibility = View.INVISIBLE
        }


        vm.isThereAnyReservation().asLiveData().observe(viewLifecycleOwner) {
            hideMainLoading()
            when (it) {
                is State.Error -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
                is State.Loading -> showMainLoading()
                is State.Success -> {
                    if (it.data!!)
                        AlertDialog.Builder(requireContext()).apply {
                            setMessage("You have an incomplete reservation?")
                            setPositiveButton("OK") { d, _ ->
                                d.dismiss()
                            }
                            setOnDismissListener {
                                findNavController().popBackStack()
                            }
                        }.show()
                }
            }
        }


        timeAdapter
        binding.apply {

            initForm()

            if (!barber.image.isNullOrBlank()) {
                glide().load(Firebase.storage.reference.child(barber.image!!)).into(ivBarberImage)
            }

            tvName.text = barber.name
            tvSalonName.text = salon.name
            tvPhone.text = barber.phone





            cvHairCut.visibility = View.GONE
            cvBeardCut.visibility = View.GONE
            cvCleaning.visibility = View.GONE
            cvColoring.visibility = View.GONE



            barber.services.forEach {
                when (it.id) {
                    ServicesType.Null -> {}
                    ServicesType.HairCut -> {
                        cvHairCut.isVisible = true
                        tvHairCutPrice.text = it.price.toMoney()
                        cvHairCut.tag = it
                    }
                    ServicesType.BeardCut -> {
                        cvBeardCut.isVisible = true
                        tvBeardCutPrice.text = it.price.toMoney()
                        cvBeardCut.tag = it

                    }
                    ServicesType.Cleaning -> {
                        cvCleaning.isVisible = true
                        tvCleaningPrice.text = it.price.toMoney()
                        cvCleaning.tag = it

                    }
                    ServicesType.Coloring -> {
                        cvColoring.isVisible = true
                        tvColoringPrice.text = it.price.toMoney()
                        cvColoring.tag = it

                    }
                }
            }


            if (cvHairCut.visibility == View.GONE)
                servicesGrid.removeView(cvHairCut)
            if (cvBeardCut.visibility == View.GONE)
                servicesGrid.removeView(cvBeardCut)
            if (cvCleaning.visibility == View.GONE)
                servicesGrid.removeView(cvCleaning)
            if (cvColoring.visibility == View.GONE)
                servicesGrid.removeView(cvColoring)


        }
    }

    private fun initForm() {
        binding.apply {

            cash.isChecked = vm.isCash
            kent.isChecked = !vm.isCash

            radioGroup.setOnCheckedChangeListener { radioGroup, i ->
                vm.isCash = cash.isChecked
            }

            listOf(cvBeardCut, cvHairCut, cvCleaning, cvColoring).forEach { cardView ->
                cardView.setOnClickListener {
                    if (vm.selectedServices.contains(cardView.tag as Service)) {
                        vm.selectedServices.remove(cardView.tag as Service)
                        cardView.setBackgroundColor(resources.getColor(R.color.white))
                    } else {
                        vm.selectedServices.add(cardView.tag as Service)
                        cardView.setBackgroundColor(resources.getColor(R.color.selectedItemColor))
                    }
                }
            }


            changeMonth()
            initDays()



            btnConfirm.setOnClickListener {
                if (vm.selectedServices.isEmpty()) {
                    Toast.makeText(context, "you must select service", Toast.LENGTH_SHORT).show()
                } else if (vm.selectedDay == null) {
                    Toast.makeText(context, "you must select day", Toast.LENGTH_SHORT).show()
                } else if (vm.selectedTime == null) {
                    Toast.makeText(context, "you must select time", Toast.LENGTH_SHORT).show()
                } else {

                    if (vm.isCash)
                        vm.addReservation(args.barber.barberId, salon.salonId, vm.selectedServices, if (vm.isCash) PaymentMethods.Cash else PaymentMethods.Kent).asLiveData()
                            .observe(viewLifecycleOwner) {
                                Toast.makeText(context, "do reservation", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(
                                    FragmentBarberServicesDirections.actionFragmentBarberServicesToPaymentDone()
                                )
                            }
                    else
                        findNavController().navigate(
                            FragmentBarberServicesDirections.actionFragmentBarberServicesToFragmentPayment2(
                                Reservation(
                                    args.barber.barberId, salon.salonId, vm.selectedServices, vm.date.time, if (vm.isCash) PaymentMethods.Cash else PaymentMethods.Kent
                                )
                            )
                        )
                }
            }

        }
    }

    private fun changeMonth() {
        binding.apply {
            btnNextMonth.setOnClickListener {
                vm.calender.add(Calendar.MONTH, 1)
                vm.calender.set(Calendar.DAY_OF_MONTH, 1)
                vm.selectedDay = null
                initDays()
            }

            btnPrevMonth.setOnClickListener {
                if (vm.calender > Calendar.getInstance()) {
                    vm.calender.add(Calendar.MONTH, -1)
                    vm.calender.set(Calendar.DAY_OF_MONTH, 1)
                    vm.selectedDay = null
                }

                if (vm.calender.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH))
                    vm.calender.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

                initDays()

            }


        }
    }


    private fun initDays() {
        binding.btnPrevMonth.isVisible = vm.calender > Calendar.getInstance()

        binding.tvMonthYear.text = SimpleDateFormat("MMMM yyyy").format(vm.calender.time)

        var daysLength = vm.calender.getActualMaximum(Calendar.DAY_OF_MONTH)

        if (vm.calender.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)) {
            daysLength = vm.calender.getActualMaximum(Calendar.DAY_OF_MONTH) - Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 1
        }

        val days = Array(daysLength) {
            Calendar.getInstance().run {
                time = vm.calender.time
                set(Calendar.DAY_OF_MONTH, vm.calender.get(Calendar.DAY_OF_MONTH) + it)
                time
            }
        }

        daysAdapter.setList(days.toList())
    }
}