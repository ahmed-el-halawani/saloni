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

private const val TAG = "SignInFragment"

class SignInFragment : BaseFragment() {
    private val vm: SignInViewModel by viewModels()
    private lateinit var binding: FragmentSignInBinding

    val args: SignInFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        form {
            input(binding.etEmail) {
                isNotEmpty()
                isEmail()
            }
            input(binding.etPassword) { isNotEmpty() }

            submitWith(binding.btnLogin) {
                if (it.success()) {
                    vm.signIn(binding.etEmail.text.toString(), binding.etPassword.text.toString(), args.isSalon).asLiveData().observe(viewLifecycleOwner) {
                        hideMainLoading()
                        when (it) {
                            is State.Error -> Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                            is State.Loading -> showMainLoading()
                            is State.Success -> navigateToHome()
                        }
                    }
                }
            }
        }


        binding.btnSignUp.setOnClickListener {
            findNavController().navigate( SignInFragmentDirections.actionSignInFragmentToSignUpFragment() )
        }
    }

    fun navigateToHome() {
        findNavController().navigate(
            if (args.isSalon) SignInFragmentDirections.actionSignInFragmentToFragmentHomeSalon()
            else SignInFragmentDirections.actionSignInFragmentToFragmentHomeClient()
        )
    }
}