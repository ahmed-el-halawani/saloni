package com.demo.saloni.ui

import android.app.ProgressDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.*


abstract class BaseFragment : Fragment() {

    val progress by lazy {
        ProgressDialog(requireContext())
    }


    open fun showLoading() {
        if (!progress.isShowing) {
            progress.show()
            CoroutineScope(Dispatchers.IO).launch {
                delay(30000)
                withContext(Dispatchers.Main) {
                    hideLoading()
                }
            }
        }
    }

    open fun hideLoading() {
        if (progress.isShowing)
            progress.dismiss()
    }
}