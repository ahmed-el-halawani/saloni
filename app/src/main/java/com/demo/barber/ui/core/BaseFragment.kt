package com.demo.barber.ui.core

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.demo.barber.MainActivity
import com.demo.barber.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


abstract class BaseFragment : Fragment(), ILoadable by Loadable() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setMainLoaderDialog((activity as MainActivity).getMainLoading())
        setSubLoaderDialog((activity as MainActivity).getMainLoading())
    }

    fun setBackButton(view: View) {
        view.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideMainLoading()
        hideSubLoading()

        findNavController().addOnDestinationChangedListener { i, d, j ->
            hideMainLoading()
            hideSubLoading()
        }
    }

    override fun onPause() {
        super.onPause()
        hideMainLoading()
        hideSubLoading()
    }

    override fun onStop() {
        super.onStop()
        hideMainLoading()
        hideSubLoading()
    }
}

@SuppressLint("CheckResult", "PrivateResource")
fun Fragment.glide(): RequestManager {
    val requestOptions = RequestOptions()
    requestOptions.placeholder(R.drawable.placeholder)
    requestOptions.error(com.google.android.material.R.drawable.mtrl_ic_error)
    return Glide.with(requireContext()).setDefaultRequestOptions(requestOptions)
}

fun Fragment.firebaseGlide(ref: String, imageView: ImageView) {
    glide().load(Firebase.storage.reference.child(ref)).into(imageView)
}