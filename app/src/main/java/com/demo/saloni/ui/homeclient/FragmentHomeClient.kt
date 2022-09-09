package com.demo.saloni.ui.homeclient

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.demo.saloni.R
import com.demo.saloni.databinding.FragmentHomeClientBinding
import com.demo.saloni.ui.core.BaseFragment
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class FragmentHomeClient : BaseFragment() {
    val vm: HomeClientViewModel by viewModels()

    private val binding: FragmentHomeClientBinding by lazy {
        FragmentHomeClientBinding.inflate(layoutInflater)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_client, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Firebase.storage.reference.child(vm.clientProfile?.image ?: "").getBytes(1024 * 1024).addOnSuccessListener {
            Glide.with(requireContext())
                .load(it)
                .into(binding.civClient)
        }
        binding.tvUsername.text = vm.clientProfile?.image
    }

}