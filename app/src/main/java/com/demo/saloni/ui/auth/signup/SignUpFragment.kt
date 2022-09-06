package com.demo.saloni.ui.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.demo.saloni.databinding.FragmentSignUpBinding
import com.google.android.material.tabs.TabLayoutMediator

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ViewPagerAdapter(parentFragmentManager,lifecycle)
        binding.viewPager2.adapter = adapter
        TabLayoutMediator(binding.tabLayout,binding.viewPager2){
            tab,position ->
            when(position){
                0->tab.text = "Salon"
                1-> tab.text = "Client"
            }
        }.attach()

    }
}