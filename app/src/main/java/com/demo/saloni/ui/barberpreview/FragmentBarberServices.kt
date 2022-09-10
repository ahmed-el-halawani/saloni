package com.demo.saloni.ui.barberpreview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.demo.saloni.R
import com.demo.saloni.data.remote.entities.Service
import com.demo.saloni.data.remote.entities.ServicesType
import com.demo.saloni.databinding.FragmentBarberServicesBinding
import com.demo.saloni.databinding.FragmentProfileBarberBinding
import com.demo.saloni.databinding.ItemCalenderBinding
import com.demo.saloni.databinding.ItemTimeBinding
import com.demo.saloni.ui.core.BaseFragment
import com.demo.saloni.ui.core.State
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.newcore.easyrecyclergenerator.rvList
import com.newcore.easyrecyclergenerator.rvSingleList
import java.text.SimpleDateFormat
import java.util.*

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

                itemCalenderBinding.container.setOnClickListener {
                    if (vm.selectedDay == null || vm.selectedDay != date) {
                        vm.currentSelectedItem?.setBackgroundColor(resources.getColor(android.R.color.transparent))
                        it.setBackgroundColor(resources.getColor(R.color.orange))
                        vm.selectedDay = date
                        vm.currentSelectedItem = it
                    }
                }
            }
        }
    }

    val timeAdapter by lazy {
        val timeFormatter = SimpleDateFormat("h:mm a")
        val timeList = Array(48) {
            timeFormatter.format(
                Calendar.getInstance().apply {
                    set(Calendar.HOUR, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.AM_PM, Calendar.AM)
                    add(Calendar.MINUTE, it * 30)
                }.time
            )
        }
        rvSingleList(binding.rvTime, ItemTimeBinding::inflate, timeList.toList(), LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)) {
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
                    vm.selectedTime = timeFormatter.parse(date);
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timeAdapter
        val barber = args.barber
        binding.apply {

            initForm()

            if (barber.image != null) {
                Glide.with(requireContext()).load(
                    Firebase.storage.reference.child(barber.image!!)
                ).into(ivBarberImage)
            }

            tvName.text = barber.name
            tvSalonName.text = args.salon.name
            tvPhone.text = barber.phone

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
                        cvHairCut.tag = it
                    }
                    ServicesType.BeardCut -> {
                        cvBeardCut.isVisible = true
                        tvBeardCutPrice.text = it.price.toString()
                        cvBeardCut.tag = it

                    }
                    ServicesType.Cleaning -> {
                        cvCleaning.isVisible = true
                        tvCleaningPrice.text = it.price.toString()
                        cvCleaning.tag = it

                    }
                    ServicesType.Coloring -> {
                        cvColoring.isVisible = true
                        tvColoringPrice.text = it.price.toString()
                        cvColoring.tag = it

                    }
                }
            }
        }
    }

    private fun initForm() {
        binding.apply {

            cash.isChecked = vm.isCash
            kent.isChecked = !vm.isCash

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

            initDays()



            btnConfirm.setOnClickListener {
                if (vm.selectedServices.isEmpty()) {
                    Toast.makeText(context, "you must select service", Toast.LENGTH_SHORT).show()
                } else if (vm.selectedDay == null) {
                    Toast.makeText(context, "you must select day", Toast.LENGTH_SHORT).show()
                } else if (vm.selectedTime == null) {
                    Toast.makeText(context, "you must select time", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "do reservation", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun initDays() {
        val days = Array(vm.calender.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            Calendar.getInstance().run {
                set(Calendar.DAY_OF_MONTH, it + 1)
                time
            }
        }

        daysAdapter.setList(days.toList())
    }
}