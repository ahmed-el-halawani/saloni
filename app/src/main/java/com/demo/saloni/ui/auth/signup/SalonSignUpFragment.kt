package com.demo.saloni.ui.auth.signup

import android.R.attr.data
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.afollestad.vvalidator.form
import com.demo.saloni.data.remote.AuthServices
import com.demo.saloni.databinding.FragmentSalonSignUpBinding
import java.io.FileNotFoundException
import java.io.InputStream


class SalonSignUpFragment : Fragment() {
    private lateinit var binding: FragmentSalonSignUpBinding
    private var imageUri: Uri? = null;
    private  val TAG = "SalonSignUpFragment"

    private var authService = AuthServices()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSalonSignUpBinding.inflate(inflater, container, false);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initProfileImage()

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
            submitWith(binding.btnSignUp) {
                if (it.success()) {
                    authService.signUpSalon(
                        imageUri,
                        binding.etSalonName.text.toString(),
                        binding.etMobileNumber.text.toString(),
                        binding.etEmail.text.toString(),
                        "facebook",
                        "insta",
                        "twitter",
                        binding.etPassword.text.toString(),
                        {
                            Log.e(TAG, "salon profile: "+it, )
                        },{
                            Log.e(TAG, "error salon profile: "+it, )
                        }
                    );
                }
            }
        }


    }

    private fun initProfileImage() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        binding.btnAddImage.setOnClickListener {
            val startForResult =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    if (it.resultCode == RESULT_OK) {
                        try {
                            imageUri = it.data?.data
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
                        Log.e(TAG, "Something went wrong: resultCode: "+it.resultCode )
                        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            startForResult.launch(photoPickerIntent)
        }
    }

}