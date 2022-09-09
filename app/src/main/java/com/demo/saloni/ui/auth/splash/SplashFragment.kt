package com.demo.saloni.ui.auth.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.demo.saloni.data.remote.AuthServices
import com.demo.saloni.databinding.ActivityMainBinding
import com.demo.saloni.R
import com.demo.saloni.databinding.FragmentSplashBinding
import com.demo.saloni.ui.auth.signin.SignInFragmentDirections
import com.demo.saloni.ui.core.BaseFragment
import com.demo.saloni.ui.core.State
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashFragment : BaseFragment() {
    private val binding by lazy {
        FragmentSplashBinding.inflate(layoutInflater)
    }

    private val vm: SplashViewModel by viewModels()
    private var topAnim: Animation? = null
    private var bottomAnim: Animation? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        bindSplashAnimation()


        if (Firebase.auth.currentUser != null) {
            vm.getProfile(Firebase.auth.currentUser!!.uid).asLiveData().observe(viewLifecycleOwner) {
                hideMainLoading()
                when (it) {
                    is State.Error -> Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    is State.Loading -> showMainLoading()
                    is State.Success -> navigateToHome(it.data?.isSalon ?: false)
                }
            }
        }

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

    fun navigateToHome(isSalon: Boolean) {
        findNavController().navigate(
            if (isSalon) SplashFragmentDirections.actionSplashFragmentToFragmentHomeSalon()
            else SplashFragmentDirections.actionSplashFragmentToFragmentHomeClient()
        )
    }

    private fun bindSplashAnimation() {
        topAnim = AnimationUtils.loadAnimation(activity, R.anim.top_animation)
        bottomAnim = AnimationUtils.loadAnimation(activity, R.anim.buttom_animation)
        binding.ivLogo.animation = topAnim
        binding.linearButtons.animation = bottomAnim
    }
}