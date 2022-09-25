package com.demo.barber.ui.auth.signup

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
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
import com.demo.barber.databinding.FragmentClientSignUpBinding
import com.demo.barber.ui.core.BaseFragment
import com.demo.barber.ui.core.State
import java.io.FileNotFoundException
import java.util.*


class ClientSignUpFragment : BaseFragment() {
    private val TAG = "ClientSignUpFragment"

    private val vm: SignUpViewModel by viewModels()
    private lateinit var binding: FragmentClientSignUpBinding
    private lateinit var startForResult: ActivityResultLauncher<Intent>;


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentClientSignUpBinding.inflate(inflater, container, false);
        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                try {
                    vm.imageUri = it.data?.data
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
        initProfileImage()
        initButtons()
        initForm()
    }


    private fun initForm() {
        vm.dataOfBirth.observe(viewLifecycleOwner) {
            binding.etDateOfBirth.setText(it)
        }
        form {
            input(binding.etEmail) {
                isNotEmpty()
                isEmail()
            }
            input(binding.etUserName) {
                isNotEmpty()
            }
            input(binding.etMobileNumber) {
                isNotEmpty()
            }
            input(binding.etCivilId) {
                isNotEmpty()
            }
            input(binding.etDateOfBirth) {
                isNotEmpty()
            }
            input(binding.etPassword) {
                isNotEmpty()
                assert("password and confirm password miss match") { view ->
                    view.text.toString() == binding.etConfirmPassword.text.toString()
                }
            }
            input(binding.etConfirmPassword) {
                isNotEmpty()
                assert("password and confirm password miss match") { view ->
                    view.text.toString() == binding.etPassword.text.toString()
                }
            }
            submitWith(binding.btnSignUp) { formResult ->
                if (formResult.success()) {
                    vm.signUpClient(
                        vm.imageUri,
                        binding.etUserName.text.toString(),
                        binding.etCivilId.text.toString(),
                        binding.etDateOfBirth.text.toString(),
                        binding.etMobileNumber.text.toString(),
                        binding.etEmail.text.toString(),
                        binding.etPassword.text.toString(),
                    ).asLiveData().observe(viewLifecycleOwner) {
                        hideMainLoading()
                        when (it) {
                            is State.Error -> Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                            is State.Loading -> showMainLoading()
                            is State.Success ->
                                findNavController().navigate(
                                    SignUpFragmentDirections.actionSignUpFragmentToSignInFragment(false)
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
                SignUpFragmentDirections.actionSignUpFragmentToSignInFragment(false)
            )
        }
        binding.etDateOfBirth.setOnClickListener {
            datePicker()
        }
    }

    private fun initProfileImage() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        binding.btnAddImage.setOnClickListener {
            startForResult.launch(photoPickerIntent)
        }
    }

    fun datePicker() {
        val newCalendar = Calendar.getInstance();
        DatePickerDialog(requireContext(), datePickerListener, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH)).show()
    }


    private val datePickerListener = OnDateSetListener { view, selectedYear, selectedMonth, selectedDay ->
        vm.dataOfBirth.value = "$selectedDay/$selectedMonth/$selectedYear"
    }
}

