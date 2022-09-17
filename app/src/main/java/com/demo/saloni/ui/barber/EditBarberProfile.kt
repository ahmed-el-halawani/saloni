package com.demo.saloni.ui.barber

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.afollestad.vvalidator.form
import com.bumptech.glide.Glide
import com.demo.saloni.data.remote.entities.Days
import com.demo.saloni.data.remote.entities.Service
import com.demo.saloni.data.remote.entities.ServicesType
import com.demo.saloni.data.remote.entities.ShiftTime
import com.demo.saloni.databinding.FragmentEditBarberProfileBinding
import com.demo.saloni.databinding.ItemBarberServicesBinding
import com.demo.saloni.ui.core.BaseFragment
import com.demo.saloni.ui.core.State
import com.demo.saloni.ui.core.glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.newcore.easyrecyclergenerator.rvSingleList
import java.io.FileNotFoundException


class EditBarberProfile : BaseFragment() {

    val binding: FragmentEditBarberProfileBinding by lazy {
        FragmentEditBarberProfileBinding.inflate(layoutInflater)
    }
    val args: EditBarberProfileArgs by navArgs()
    val vm: EditBarberViewModel by viewModels()

    private val TAG = "FragmentAddBarber"

    private lateinit var startForResult: ActivityResultLauncher<Intent>;


    private val servicesAdapter by lazy {
        rvSingleList(
            binding.rvServices,
            ItemBarberServicesBinding::inflate,
            emptyList<Service>(),
        ) {
            this.listBuilder { itemServicesBinding, service ->
                itemServicesBinding.tvServiceTitle.text = service.id.title
                itemServicesBinding.tvPrice.text = service.price.toString()
                itemServicesBinding.btnRemove.setOnClickListener {
                    vm.selectedServices.remove(service)
                    refreshServiceList()
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setBackButton(binding.btnBack)

        binding.spServices.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ServicesType.values().map { it.title })

        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                try {
                    vm.imageUri = it.data?.data
                    Log.e(TAG, "image.lastPathSegment: " + vm.imageUri?.lastPathSegment)
                    binding.ivProfileImage.setImageURI(vm.imageUri)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    Toast.makeText(
                        requireContext(),
                        "Something went wrong",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Log.e(TAG, "Something went wrong: resultCode: " + it.resultCode)
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_LONG)
                    .show()
            }
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBarberData(savedInstanceState == null)


        initForm()
        changeAmPm()
        initProfileImage()
        addServiceForm()
        addDays()


    }

    private fun addDays() {
        binding.tags.setOnSelectListener { themedButton ->
            if (themedButton.isSelected)
                vm.selectedDays.add(Days(themedButton.tag.toString(), themedButton.text))
            else
                vm.selectedDays.removeIf { it.dayIndex == themedButton.tag.toString() }
        }
    }

    private fun initBarberData(isFirstTime: Boolean) {
        val barber = args.barber
        binding.apply {
            if (vm.imageUri == null)
                if (!barber.image.isNullOrBlank()) {
                    glide().load(Firebase.storage.reference.child(barber.image!!)).into(ivProfileImage)
                }

            refreshServiceList()

            val days = listOf(tag1, tag2, tag3, tag4, tag5, tag6, tag7)

            vm.selectedDays.forEach {
                tags.selectButton(days[it.dayIndex.toInt() - 1])
            }

            if (isFirstTime) {
                etUserName.setText(barber.name)
                etMobileNumber.setText(barber.phone)
                etCivilId.setText(barber.civilId)


                barber.shiftStartIn?.apply {
                    etStartHour.setText(hour)
                    etStartMinit.setText(minut)
                    etStartAmOrPm.setText(amOrPm)
                }

                barber.shiftEntIn?.apply {
                    etEndHour.setText(hour)
                    etEndMint.setText(minut)
                    etEndAmOrPm.setText(amOrPm)
                }
            }

        }
    }


    private fun addServiceForm() {
        form {
            spinner(binding.spServices) {
                this.assert("No More Services") { it.count > 1 }
                this.assert("must select item") { it.selectedItem != null && it.selectedItem.toString() != ServicesType.Null.title }

                this.onErrors { view, errors ->
                    (view.selectedView as TextView).error = errors.firstOrNull()?.description
                }
            }

            input(binding.etPrice) {
                isNotEmpty()
                isDecimal().greaterThan(0.0)
            }

            input(binding.etStartMinit) {
                isNotEmpty()
                isNumber().lessThan(60).greaterThan(-1)
            }

            input(binding.etStartHour) {
                isNotEmpty()
                isNumber().lessThan(60).greaterThan(-1)
            }

            input(binding.etEndMint) {
                isNotEmpty()
                isNumber().lessThan(60).greaterThan(-1)
            }

            input(binding.etEndHour) {
                isNotEmpty()
                isNumber().lessThan(60).greaterThan(-1)
            }

            submitWith(binding.btnAddServices) {
                Log.e(TAG, "onViewCreated: $it")

                if (it.success()) {
                    val servicesType = ServicesType.values().firstOrNull { it.title == binding.spServices.selectedItem.toString() } ?: throw Exception("type not found")

                    val price = binding.etPrice.text.toString().toDouble()

                    vm.selectedServices.add(Service(servicesType, price))

                    refreshServiceList()
                } else {
                    Log.e(TAG, "onViewCreated: $it")
                }
            }

        }
    }

    private fun refreshServiceList() {
        servicesAdapter.setList(vm.selectedServices.toList())

        binding.spServices.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ServicesType.values().filter { service -> !vm.selectedServices.any { service == it.id } }.map { it.title })

    }

    private fun initForm() {
        form {
            input(binding.etUserName) {
                isNotEmpty()
            }
            input(binding.etMobileNumber) {
                isNotEmpty()
            }
            input(binding.etCivilId) {
                isNotEmpty()
            }

            submitWith(binding.btnSave) { formResult ->
                if (formResult.success()) {
                    val workingDays = vm.selectedDays
                    if (workingDays.isEmpty()) {
                        Toast.makeText(context, "must select working days", Toast.LENGTH_SHORT).show()
                    } else if (vm.selectedServices.isEmpty()) {
                        Toast.makeText(context, "must add services", Toast.LENGTH_SHORT).show()
                    } else {
                        vm.editBarber(
                            args.barber.barberId,
                            binding.etUserName.text.toString(),
                            binding.etMobileNumber.text.toString(),
                            binding.etCivilId.text.toString(),
                            vm.imageUri,
                            workingDays,
                            vm.selectedServices,
                            getShiftStartIn(),
                            getShiftEndIn()
                        ).asLiveData().observe(viewLifecycleOwner) {
                            hideMainLoading()
                            when (it) {
                                is State.Error -> Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                                is State.Loading -> showMainLoading()
                                is State.Success -> {
                                    Toast.makeText(context, "Barber edited Successfully", Toast.LENGTH_SHORT).show()
                                    findNavController().popBackStack()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initProfileImage() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        binding.btnAddImage.setOnClickListener {
            startForResult.launch(photoPickerIntent)
        }
    }

    fun getShiftStartIn(): ShiftTime {
        return ShiftTime(binding.etStartHour.text.toString(), binding.etStartMinit.text.toString(), binding.etStartAmOrPm.text.toString())
    }

    fun getShiftEndIn(): ShiftTime {
        return ShiftTime(binding.etEndHour.text.toString(), binding.etEndMint.text.toString(), binding.etEndAmOrPm.text.toString())
    }


    fun changeAmPm() {
        binding.btnStartAm.setOnClickListener {
            binding.etStartAmOrPm.setText("AM")
        }
        binding.btnStartPm.setOnClickListener {
            binding.etStartAmOrPm.setText("PM")
        }
        binding.btnEndAm.setOnClickListener {
            binding.etEndAmOrPm.setText("AM")
        }
        binding.btnEndPm.setOnClickListener {
            binding.etEndAmOrPm.setText("PM")
        }
    }

}