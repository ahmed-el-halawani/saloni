package com.demo.saloni.ui.auth.signup

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.demo.saloni.databinding.FragmentSalonSignUpBinding
import com.demo.saloni.ui.core.BaseFragment
import com.demo.saloni.ui.core.State
import java.io.FileNotFoundException


class SalonSignUpFragment : BaseFragment() {
    private lateinit var binding: FragmentSalonSignUpBinding
    private var imageUri: Uri? = null;
    private val TAG = "SalonSignUpFragment"

    private val vm: SignUpViewModel by viewModels()
    private lateinit var startForResult: ActivityResultLauncher<Intent>;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSalonSignUpBinding.inflate(inflater, container, false);

        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                try {
                    imageUri = it.data?.data
                    Log.e(TAG, "image.lastPathSegment: " + imageUri?.lastPathSegment)
                    binding.ivProfileImage.setImageURI(imageUri)
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
        initProfileImage()
        initButtons()
        initForm()

    }

    private fun initForm() {
        form {
            input(binding.etEmail) {
                isNotEmpty()
                isEmail()
            }
            input(binding.etAddress) {
                isNotEmpty()
            }
            input(binding.etMobileNumber) {
                isNotEmpty()
            }
            input(binding.etSalonName) {
                isNotEmpty()
            }
            input(binding.etPassword) {
                isNotEmpty()
            }
            input(binding.etConfirmPassword) {
                isNotEmpty()
                assert("password and confirm password miss match") { view ->
                    view.text.toString() == binding.etPassword.text.toString()
                }
            }
            submitWith(binding.btnSignUp) { formResult ->
                if (formResult.success()) {
                    vm.signUpSalon(
                        imageUri,
                        binding.etSalonName.text.toString(),
                        binding.etMobileNumber.text.toString(),
                        binding.etEmail.text.toString(),
                        binding.etAddress.text.toString(),
                        binding.etPassword.text.toString(),
                        binding.etFacebook.text.toString(),
                        binding.etInsta.text.toString(),
                        binding.etTwitter.text.toString()
                    )
                        .asLiveData().observe(viewLifecycleOwner) {
                            hideMainLoading()
                            when (it) {
                                is State.Error -> Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                                is State.Loading -> showMainLoading()
                                is State.Success ->
                                    findNavController().navigate(
                                        SignUpFragmentDirections.actionSignUpFragmentToSignInFragment(true)
                                    )
                            }
                        }
                }
            }
        }
    }

    private fun initButtons() {
        binding.btnLogin.setOnClickListener {
            findNavController().navigate(
                SignUpFragmentDirections.actionSignUpFragmentToSignInFragment(true)
            )
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