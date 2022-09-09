package com.demo.saloni.ui.auth.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.demo.saloni.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {
    private val binding by lazy {
        FragmentSplashBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLoginAsUser.setOnClickListener {
            findNavController().navigate(
                SplashFragmentDirections
                    .actionSplashFragmentToSignInFragment(false)
            )
        }

        binding.btnLoginAsSalon.setOnClickListener {
            findNavController().navigate(
                SplashFragmentDirections
                    .actionSplashFragmentToSignInFragment(true)
            )
        }


    }
}