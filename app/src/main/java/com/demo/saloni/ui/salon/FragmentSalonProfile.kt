package com.demo.saloni.ui.salon

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.demo.saloni.databinding.FragmentSalonProfileBinding
import com.demo.saloni.ui.core.BaseFragment
import com.demo.saloni.ui.core.glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class FragmentSalonProfile : BaseFragment() {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            if (!vm.salonProfile?.image.isNullOrBlank())
                glide().load(Firebase.storage.reference.child(vm.salonProfile?.image!!)).into(binding.ivSalonProfileImage)

            if (vm.salonProfile != null) {
                val salon = vm.salonProfile!!
                tvSalonName.text = vm.salonProfile!!.name

                tvPhoneNumber.text = vm.salonProfile!!.phoneNumber
                tvEmail.text = vm.salonProfile!!.email
                tvAddress.text = vm.salonProfile!!.address

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