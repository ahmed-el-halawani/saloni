package com.demo.saloni.ui.salon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.demo.saloni.databinding.FragmentEditSalonProfileBinding
import com.demo.saloni.ui.core.BaseFragment

class FragmentEditSalon : BaseFragment() {

    val binding by lazy {
        FragmentEditSalonProfileBinding.inflate(layoutInflater)
    }
    val vm: EditSalonProfileViewModel by viewModels()

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
        bindView()

    }

    private fun bindView() {
        binding.apply {
            etSalonName.setText(vm.salonProfile!!.name)
            etMobileNumber.setText(vm.salonProfile!!.name)
            etAddress.setText(vm.salonProfile!!.name)

        }
    }


}