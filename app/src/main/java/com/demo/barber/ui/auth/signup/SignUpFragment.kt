package com.demo.barber.ui.auth.signup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.demo.barber.databinding.FragmentSignUpBinding
import com.demo.barber.ui.core.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.delay

class SignUpFragment : BaseFragment() {
    private lateinit var binding: FragmentSignUpBinding

    private val args: SignUpFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        setBackButton(binding.btnBack)

        val adapter = ViewPagerAdapter(parentFragmentManager, lifecycle)
        binding.viewPager2.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = "Salon"
                1 -> tab.text = "Client"
            }
        }.attach()

        return binding.root
    }

    private val TAG = "SignUpFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        Log.e(TAG, "isSalon: " + args.isSalon)
        Log.e(TAG, "binding.viewPager2.currentItem: " + binding.viewPager2.currentItem)

        lifecycleScope.launchWhenCreated {
            delay(100)
            binding.viewPager2.currentItem = if (args.isSalon) 0 else 1
        }

    }

    fun navigateToLogin(isSalon: Boolean) {
        findNavController().navigate(
            SignUpFragmentDirections.actionSignUpFragmentToSignInFragment(isSalon)
        )
    }
}