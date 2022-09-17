package com.demo.saloni.ui.clientprofile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.afollestad.vvalidator.form
import com.demo.saloni.R
import com.demo.saloni.data.remote.entities.ClientProfile
import com.demo.saloni.data.remote.entities.SalonProfile
import com.demo.saloni.databinding.FragmentClientProfileBinding
import com.demo.saloni.databinding.FragmentEditProfileClientBinding
import com.demo.saloni.ui.core.BaseFragment
import com.demo.saloni.ui.core.State
import com.demo.saloni.ui.core.firebaseGlide
import com.demo.saloni.ui.salon.EditSalonProfileViewModel
import java.io.FileNotFoundException


class FragmentEditProfileClient : BaseFragment() {
    val binding by lazy {
        FragmentEditProfileClientBinding.inflate(layoutInflater)
    }

    private val TAG = "FragmentEditProfileClie"
    private lateinit var startForResult: ActivityResultLauncher<Intent>;

    val vm: ClientProfileViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setBackButton(binding.btnBack)
        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
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
        initView()
        initProfileImage()
        initForm()
    }


    private fun initView() {
        binding.apply {
            vm.profile.apply {

                if (!image.isNullOrBlank())
                    firebaseGlide(image!!, binding.ivProfileImage)

                etMobileNumber.setText(phoneNumber)
                etSalonName.setText(name)
                etDateOfBirth.setText(dataOfBirth)
                binding.etCivilId.setText(civilId)
            }


        }
    }


    private fun initForm() {
        form {

            input(binding.etMobileNumber) {
                isNotEmpty()
            }
            input(binding.etSalonName) {
                isNotEmpty()
            }

            input(binding.etDateOfBirth) {
                isNotEmpty()
            }

            input(binding.etCivilId) {
                isNotEmpty()
            }
            submitWith(binding.btnEditProfile) { formResult ->
                if (formResult.success()) {
                    vm.editClient(
                        ClientProfile().apply {
                            name = binding.etSalonName.text.toString()
                            phoneNumber = binding.etMobileNumber.text.toString()
                            dataOfBirth = binding.etDateOfBirth.text.toString()
                            civilId = binding.etCivilId.text.toString()
                        }
                    ).asLiveData().observe(viewLifecycleOwner) {
                        hideMainLoading()
                        when (it) {
                            is State.Error -> Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                            is State.Loading -> showMainLoading()
                            is State.Success ->
                                findNavController().popBackStack()
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
}