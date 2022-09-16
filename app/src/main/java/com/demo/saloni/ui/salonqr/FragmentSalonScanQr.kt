package com.demo.saloni.ui.salonqr

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.demo.saloni.R
import com.demo.saloni.databinding.FragmentSalonScanQrBinding

class FragmentSalonScanQr : Fragment() {
    val binding by lazy {
        FragmentSalonScanQrBinding.inflate(layoutInflater)
    }

    val vm: SalonScanQrViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initScanner()
        vm.getReservations(it.text).asLiveData().observe(viewLifecycleOwner) {
            Log.e(TAG, "initScanner: " + it)
        }

    }

    private val TAG = "FragmentSalonScanQr"
    private lateinit var codeScanner: CodeScanner

    fun initScanner() {


        val scannerView = binding.scannerView

        codeScanner = CodeScanner(requireContext(), scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {

            codeScanner.stopPreview()
        }

        codeScanner.startPreview()


//        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
//            runOnUiThread {
//                Toast.makeText(this, "Camera initialization error: ${it.message}",
//                    Toast.LENGTH_LONG).show()
//            }
//        }


    }


}