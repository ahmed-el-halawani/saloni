package com.demo.saloni.ui.clientprofile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import com.demo.saloni.R
import com.demo.saloni.data.local.CashedData
import com.demo.saloni.databinding.FragmentClientProfileBinding
import com.demo.saloni.ui.core.BaseFragment
import com.demo.saloni.ui.core.firebaseGlide
import com.demo.saloni.ui.core.glide
import com.demo.saloni.ui.homeclient.FragmentHomeClientDirections
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FragmentClientProfile : BaseFragment() {
    val binding by lazy {
        FragmentClientProfileBinding.inflate(layoutInflater)
    }

    val vm: ClientProfileViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setBackButton(binding.btnBack)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    private val TAG = "FragmentClientProfile"
    private fun initView() {
        try {
            binding.apply {
                CashedData.clientProfile!!.apply {

                    if (!image.isNullOrBlank())
                        firebaseGlide(image!!, binding.ivBarberImage)

                    tvPhoneNumber.text = phoneNumber
                    tvEmail.text = email
                    tvName.text = name
                    tvCivilId.text = civilId

                    Log.e(TAG, "initView: ")

                    btnEdit.setOnClickListener {
                        findNavController().addOnDestinationChangedListener { controller, destination, arguments ->
                            if (destination.id == R.id.fragmentClientProfile) {
                                Log.e(TAG, "initView: " + destination.id + ": " + R.id.fragmentClientProfile)
                                try {
                                    initView()
                                } catch (e: Exception) {
                                }

                            }
                        }
                        findNavController().navigate(
                            FragmentClientProfileDirections.actionFragmentClientProfileToFragmentEditProfileClient()
                        )
                    }

                    btnLogout.setOnClickListener {
                        Firebase.auth.signOut()

                        findNavController().navigate(
                            FragmentClientProfileDirections.actionFragmentClientProfileToSplashFragment()
                        )
                    }

                }


            }
        } catch (e: Exception) {
        }
    }


}