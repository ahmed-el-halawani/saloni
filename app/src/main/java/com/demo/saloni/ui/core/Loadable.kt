package com.demo.saloni.ui.core

import android.app.Dialog
import android.util.Log
import kotlinx.coroutines.*

interface ILoadable {
    fun setMainLoaderDialog(mainLoaderViewDialog: Dialog)
    fun setSubLoaderDialog(subLoaderViewDialog: Dialog)
    fun showMainLoading()
    fun hideMainLoading()
    fun showSubLoading()
    fun hideSubLoading()
}

class Loadable() : ILoadable {
    private val TAG = "Loadable"
    private val mainLoadingJob = CoroutineScope(Dispatchers.IO)
    private var mainLoaderViewDialog: Dialog? = null;
    private val subLoadingJob = CoroutineScope(Dispatchers.IO)
    private var subLoaderViewDialog: Dialog? = null;


    override fun setMainLoaderDialog(mainLoaderViewDialog: Dialog) {
        this.mainLoaderViewDialog = mainLoaderViewDialog;
    }

    override fun setSubLoaderDialog(subLoaderViewDialog: Dialog) {
        this.subLoaderViewDialog = subLoaderViewDialog;
    }

    override fun showMainLoading() {
        if (mainLoaderViewDialog == null) {
            Log.e(TAG, "showMainLoading: please mainLoader not init yet")
            return;
        }
        mainLoaderViewDialog?.show()
        mainLoadingJob.cancel()
        mainLoadingJob.launch {
            delay(30000)
            if (!isActive) {
                withContext(Dispatchers.Main) {
                    mainLoaderViewDialog?.hide()
                }
            }

        }
    }

    override fun hideMainLoading() {
        mainLoaderViewDialog?.hide()
        mainLoadingJob.cancel()

    }

    override fun showSubLoading() {
        if (subLoaderViewDialog == null) {
            Log.e(TAG, "showMainLoading: please subLoader not init yet")
            return;
        }
        subLoaderViewDialog?.show()
        subLoadingJob.cancel()
        subLoadingJob.launch {
            delay(30000)
            if (!isActive){
                withContext(Dispatchers.Main) {
                    subLoaderViewDialog?.hide()
                }
            }
        }
    }

    override fun hideSubLoading() {
        subLoaderViewDialog?.hide()
        subLoadingJob.cancel()

    }
}