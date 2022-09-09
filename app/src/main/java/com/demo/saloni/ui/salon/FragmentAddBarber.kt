package com.demo.saloni.ui.salon

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
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
import com.afollestad.vvalidator.form
import com.demo.saloni.R
import com.demo.saloni.data.remote.entities.Barber
import com.demo.saloni.data.remote.entities.Days
import com.demo.saloni.data.remote.entities.Service
import com.demo.saloni.data.remote.entities.ServicesType
import com.demo.saloni.databinding.*
import com.demo.saloni.ui.auth.signup.SignUpFragmentDirections
import com.demo.saloni.ui.core.BaseFragment
import com.demo.saloni.ui.core.State
import com.newcore.easyrecyclergenerator.rvList
import com.newcore.easyrecyclergenerator.rvSingleList
import java.io.FileNotFoundException
import java.util.*

class FragmentAddBarber : BaseFragment() {
    private val TAG = "FragmentAddBarber"
    val binding by lazy {
        FragmentAddBarberBinding.inflate(layoutInflater)
    }
    private lateinit var startForResult: ActivityResultLauncher<Intent>;

    val vm: AddBarberViewModel by viewModels()

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
        binding.spServices.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ServicesType.values().map { it.title })

        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                try {
                    vm.imageUri = it.data?.data
                    Log.e(TAG, "image.lastPathSegment: " + vm.imageUri?.lastPathSegment)
                    binding.ivUserProfileImage.setImageURI(vm.imageUri)
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
        initForm()
        changeAmPm()
        initProfileImage()
        addServiceForm()


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

            input(binding.etStartMinute) {
                isNotEmpty()
                isNumber().lessThan(60).greaterThan(-1)
            }

            input(binding.etHourTime) {
                isNotEmpty()
                isNumber().lessThan(60).greaterThan(-1)
            }

            input(binding.etEndMinet) {
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

            submitWith(binding.btnAddBarber) { formResult ->
                if (formResult.success()) {
                    val workingDays = binding.tags.buttons.filter { it.isSelected }
                    if (workingDays.isEmpty()) {
                        Toast.makeText(context, "must select working days", Toast.LENGTH_SHORT).show()
                    } else if (vm.selectedServices.isEmpty()) {
                        Toast.makeText(context, "must add services", Toast.LENGTH_SHORT).show()
                    } else {
                        vm.addBarber(
                            binding.etUserName.text.toString(),
                            binding.etMobileNumber.text.toString(),
                            binding.etCivilId.text.toString(),
                            null,
                            workingDays.map { Days(it.tag.toString(), it.text) },
                            vm.selectedServices,
                            getShiftStartIn(),
                            getShiftEndIn()
                        ).asLiveData().observe(viewLifecycleOwner) {
                            hideMainLoading()
                            when (it) {
                                is State.Error -> Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                                is State.Loading -> showMainLoading()
                                is State.Success -> {
                                    Toast.makeText(context, "Barber added successfully", Toast.LENGTH_SHORT).show()
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

    fun getShiftStartIn(): String {
        return String.format("%s:%s %s", binding.etHourTime.text, binding.etStartMinute.text, binding.etStartPmOrAm.text)
    }

    fun getShiftEndIn(): String {
        return String.format("%s:%s %s", binding.etEndHour.text, binding.etEndMinet.text, binding.etEndAmOrPm.text)
    }

    fun changeAmPm() {
        binding.btnStartAm.setOnClickListener {
            binding.etStartPmOrAm.text = "AM"
        }
        binding.btnStartPm.setOnClickListener {
            binding.etStartPmOrAm.text = "PM"
        }
        binding.btnEndAm.setOnClickListener {
            binding.etEndAmOrPm.text = "AM"
        }
        binding.btnEndPm.setOnClickListener {
            binding.etEndAmOrPm.text = "PM"
        }
    }

}