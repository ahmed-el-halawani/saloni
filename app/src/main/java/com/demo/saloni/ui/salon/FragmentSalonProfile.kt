package com.demo.saloni.ui.salon

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.demo.saloni.R
import com.demo.saloni.data.local.CashedData
import com.demo.saloni.databinding.FragmentSalonProfileBinding
import com.demo.saloni.ui.core.BaseFragment
import com.demo.saloni.ui.core.glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class FragmentSalonProfile : BaseFragment() {
    private val TAG = "FragmentSalonProfile"
    val binding by lazy {
        FragmentSalonProfileBinding.inflate(layoutInflater)
    }
    val vm: SalonProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setBackButton(binding.btnBack)

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        initView()
    }

    private fun initView() {
        binding.apply {

            if (!CashedData.salonProfile?.image.isNullOrBlank())
                glide().load(Firebase.storage.reference.child(CashedData.salonProfile?.image!!)).into(binding.ivSalonProfileImage)

            if (CashedData.salonProfile != null) {
                val salon = CashedData.salonProfile!!
                tvSalonName.text = CashedData.salonProfile!!.name

                tvPhoneNumber.text = CashedData.salonProfile!!.phoneNumber
                tvEmail.text = CashedData.salonProfile!!.email
                tvAddress.text = CashedData.salonProfile!!.address

                btnInsta.setOnClickListener {
                    setUrlView(salon.instagram)
                }
                btnFacebook.setOnClickListener {
                    setUrlView(salon.facebook)
                }

                btnTwitter.setOnClickListener {
                    setUrlView(salon.twitter)
                }

            }


            binding.btnLogout.setOnClickListener {
                Firebase.auth.signOut()
                findNavController().navigate(
                    FragmentSalonProfileDirections.actionFragmentSalonProfile2ToSplashFragment()
                )
            }

            binding.btnEditSalon.setOnClickListener {
                findNavController().addOnDestinationChangedListener { controller, destination, arguments ->
                    if (destination.id == R.id.fragmentSalonProfile2) {
                        Log.e(TAG, "initView: " + destination.id + ": " + R.id.fragmentClientProfile)
                        try {
                            initView()
                        } catch (e: Exception) {
                        }
                    }
                }
                findNavController().navigate(FragmentSalonProfileDirections.actionFragmentSalonProfile2ToFragmentEditSalon())
            }


        }
    }

    private fun setUrlView(urlLink: String) {
        var url = urlLink
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;

        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent)
    }
}