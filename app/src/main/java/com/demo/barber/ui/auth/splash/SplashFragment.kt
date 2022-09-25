package com.demo.barber.ui.auth.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.demo.barber.MainActivity
import com.demo.barber.R
import com.demo.barber.databinding.FragmentSplashBinding
import com.demo.barber.ui.core.BaseFragment
import com.demo.barber.ui.core.State
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
                    is State.Loading -> (activity as MainActivity).getMainLoading().isVisible = true

                    is State.Success -> {
                        navigateToHome(it.data?.salon ?: false)
                    }
                }
            }
        } else {

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