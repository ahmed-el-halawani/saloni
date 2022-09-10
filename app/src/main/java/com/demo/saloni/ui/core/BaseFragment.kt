package com.demo.saloni.ui.core

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment


abstract class BaseFragment : Fragment() ,ILoadable by Loadable() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setMainLoaderDialog(ProgressDialog(context).apply {
            setTitle("please wait")
        })
        setSubLoaderDialog(ProgressDialog(context).apply {
            setTitle("please wait")
        })
    }
}