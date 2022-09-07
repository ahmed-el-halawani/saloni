package com.demo.saloni.ui.homeslone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.demo.saloni.R
import com.demo.saloni.databinding.FragmentHomeSalonBinding
import com.demo.saloni.ui.BaseFragment

class FragmentHomeClient : BaseFragment() {

    val binding by lazy {
        FragmentHomeSalonBinding.inflate(layoutInflater)
    }

    val vm:HomeSalonViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}