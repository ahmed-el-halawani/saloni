package com.demo.saloni.ui.core

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.demo.saloni.R


abstract class BaseFragment : Fragment(), ILoadable by Loadable() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setMainLoaderDialog(ProgressDialog(context).apply {
            setTitle("please wait")
        })
        setSubLoaderDialog(ProgressDialog(context).apply {
            setTitle("please wait")
        })
    }

    fun setBackButton(view: View) {
        view.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}

@SuppressLint("CheckResult", "PrivateResource")
fun Fragment.glide(): RequestManager {
    val requestOptions = RequestOptions()
    requestOptions.placeholder(R.drawable.placeholder)
    requestOptions.error(com.google.android.material.R.drawable.mtrl_ic_error)
    return Glide.with(requireContext()).setDefaultRequestOptions(requestOptions)
}