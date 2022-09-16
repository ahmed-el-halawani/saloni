package com.demo.saloni.ui.auth.signin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.afollestad.vvalidator.form
import com.demo.saloni.data.remote.AuthServices
import com.demo.saloni.databinding.FragmentSignInBinding
import com.demo.saloni.ui.core.BaseFragment
import com.demo.saloni.ui.core.State
import com.google.firebase.database.snapshot.BooleanNode

private const val TAG = "SignInFragment"

class SignInFragment : BaseFragment() {
    private val vm: SignInViewModel by viewModels()
    private lateinit var binding: FragmentSignInBinding

    val args: SignInFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        test();

        form {
            input(binding.etEmail) {
                isNotEmpty()
                isEmail()
            }
            input(binding.etPassword) { isNotEmpty() }

            submitWith(binding.btnLogin) {
                if (it.success()) {
                    login(binding.etEmail.text.toString(), binding.etPassword.text.toString())
                }
            }

        }


        binding.btnSignUp.setOnClickListener {
            findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToSignUpFragment())
        }
    }

    private fun login(user:String,password:String) {
        vm.signIn(user,password).asLiveData().observe(viewLifecycleOwner) {
            hideMainLoading()
            when (it) {
                is State.Error -> Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                is State.Loading -> showMainLoading()
                is State.Success -> navigateToHome(it.data!!.salon)
            }
        }
    }

    private fun navigateToHome(isSalon: Boolean) {
        findNavController().navigate(
            if (isSalon) SignInFragmentDirections.actionSignInFragmentToFragmentHomeSalon()
            else SignInFragmentDirections.actionSignInFragmentToFragmentHomeClient()
        )
    }

    fun test(){
        binding.apply {

            btnClient.setOnClickListener {
                binding.etEmail.setText("client@gmail.com")
                binding.etPassword.setText("12345678")
            }

            btnSalon.setOnClickListener {
                binding.etEmail.setText("user@gmail.com")
                binding.etPassword.setText("12345678")
            }
        }
    }



}