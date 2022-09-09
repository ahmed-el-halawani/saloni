package com.demo.saloni.ui.auth.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.demo.saloni.R
import com.demo.saloni.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {
    private val binding by lazy {
        FragmentSplashBinding.inflate(layoutInflater)
    }

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

    private fun bindSplashAnimation() {
        topAnim = AnimationUtils.loadAnimation(activity, R.anim.top_animation)
        bottomAnim = AnimationUtils.loadAnimation(activity, R.anim.buttom_animation)
        binding.ivLogo.animation = topAnim
        binding.linearButtons.animation = bottomAnim
    }
}