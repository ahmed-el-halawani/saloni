package com.demo.saloni.ui.core

import android.app.Dialog
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import kotlinx.coroutines.*

interface ILoadable {
    fun setMainLoaderDialog(mainLoaderViewDialog: View)
    fun setSubLoaderDialog(subLoaderViewDialog: View)
    fun showMainLoading()
    fun hideMainLoading()
    fun showSubLoading()
    fun hideSubLoading()
}

class Loadable : ILoadable {
    private val TAG = "Loadable"
    private val mainLoadingJob = CoroutineScope(Dispatchers.IO)
    private var mainLoaderViewDialog: View? = null;
    private val subLoadingJob = CoroutineScope(Dispatchers.IO)
    private var subLoaderViewDialog: View? = null;


    override fun setMainLoaderDialog(mainLoaderViewDialog: View) {
        this.mainLoaderViewDialog = mainLoaderViewDialog;
    }

    override fun setSubLoaderDialog(subLoaderViewDialog: View) {
        this.subLoaderViewDialog = subLoaderViewDialog;
    }

    override fun showMainLoading() {

        if (mainLoaderViewDialog == null) {
            Log.e(TAG, "showMainLoading: please mainLoader not init yet")
            return;
        }
        mainLoaderViewDialog?.isVisible = true
        mainLoadingJob.cancel()
        mainLoadingJob.launch {
            delay(30000)
            if (!isActive) {
                withContext(Dispatchers.Main) {
                    hideMainLoading()
                }
            }

        }
    }

    override fun hideMainLoading() {
        Log.e(TAG, "hideMainLoading: please mainLoader not init yet")

        mainLoaderViewDialog?.isVisible = false
        mainLoadingJob.cancel()

    }

    override fun showSubLoading() {
        if (subLoaderViewDialog == null) {
            Log.e(TAG, "showSubLoading: please subLoader not init yet")
            return;
        }
        subLoaderViewDialog?.isVisible = true
        subLoadingJob.cancel()
        subLoadingJob.launch {
            delay(30000)
            if (!isActive) {
                withContext(Dispatchers.Main) {
                    hideSubLoading()
                }
            }
        }
    }

    override fun hideSubLoading() {
        subLoaderViewDialog?.isVisible = false
        subLoadingJob.cancel()

    }
}