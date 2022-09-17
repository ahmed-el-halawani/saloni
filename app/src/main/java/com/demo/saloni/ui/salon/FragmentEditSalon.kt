package com.demo.saloni.ui.salon

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.afollestad.vvalidator.form
import com.bumptech.glide.Glide
import com.demo.saloni.data.remote.entities.SalonProfile
import com.demo.saloni.databinding.FragmentEditSalonProfileBinding
import com.demo.saloni.ui.auth.signup.SignUpFragmentDirections
import com.demo.saloni.ui.core.BaseFragment
import com.demo.saloni.ui.core.State
import com.demo.saloni.ui.core.glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.FileNotFoundException


class FragmentEditSalon : BaseFragment() {
    private val TAG = "FragmentEditSalon"
    val binding by lazy {
        FragmentEditSalonProfileBinding.inflate(layoutInflater)
    }
    val vm: EditSalonProfileViewModel by viewModels()
    private lateinit var startForResult: ActivityResultLauncher<Intent>;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setBackButton(binding.btnBack)
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
        bindView()
        initProfileImage()
        initForm()

    }

    private fun bindView() {
        binding.apply {
            if (!vm.salonProfile?.image.isNullOrEmpty()) {
                glide()
                    .load(Firebase.storage.reference.child(vm.salonProfile!!.image!!))
                    .into(binding.ivProfileImage)
            }
            etSalonName.setText(vm.salonProfile!!.name)
            etMobileNumber.setText(vm.salonProfile!!.phoneNumber)
            etAddress.setText(vm.salonProfile!!.address)
            binding.etFacebook.setText(vm.salonProfile!!.facebook)
            binding.etInsta.setText(vm.salonProfile!!.instagram)
            binding.etTwitter.setText(vm.salonProfile!!.twitter)
        }
    }

    private fun initForm() {
        form {
            input(binding.etAddress) {
                isNotEmpty()
            }
            input(binding.etMobileNumber) {
                isNotEmpty()
            }
            input(binding.etSalonName) {
                isNotEmpty()
            }

            submitWith(binding.btnEditProfile) { formResult ->
                if (formResult.success()) {


                    vm.editSalon(

                        SalonProfile().apply {
                            name = binding.etSalonName.text.toString()
                            phoneNumber = binding.etMobileNumber.text.toString()
                            address = binding.etAddress.text.toString()
                            facebook = binding.etAddress.text.toString()
                            instagram = binding.etInsta.text.toString()
                            twitter = binding.etTwitter.text.toString()
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