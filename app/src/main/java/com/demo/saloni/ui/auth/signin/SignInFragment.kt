package com.demo.saloni.ui.auth.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.demo.saloni.data.remote.AuthServices
import com.demo.saloni.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {

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

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text
            val password = binding.etPassword.text
            if (email.isNullOrBlank()) {
                binding.etEmail.error = "email must not be empty"
            } else if (password.isNullOrBlank())
                binding.etPassword.error = "password must not be empty"
            if (args.isSalon)
                AuthServices().signInSalon(email.toString(), password.toString(),
                    {

                    },

                    {

                    });
            else
                AuthServices().signInClient(email.toString(), password.toString(),
                    {

                    },

                    {

                    });


        }


    }
}