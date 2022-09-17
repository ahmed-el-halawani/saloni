package com.demo.saloni.ui.salonqr

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.bumptech.glide.Glide
import com.demo.saloni.R
import com.demo.saloni.databinding.FragmentSalonScanQrBinding
import com.demo.saloni.databinding.ItemServicesBinding
import com.demo.saloni.ui.core.BaseFragment
import com.demo.saloni.ui.core.State
import com.demo.saloni.ui.core.glide
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.newcore.easyrecyclergenerator.rvSingleList
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class FragmentSalonScanQr : BaseFragment() {
    val binding by lazy {
        FragmentSalonScanQrBinding.inflate(layoutInflater)
    }

    val dayNumberFormatter = SimpleDateFormat("dd MMMM,yyyy")
    val timeFormatter = SimpleDateFormat("h:mm a")

    val vm: SalonScanQrViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setBackButton(binding.btnBack)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initScanner()

        vm.barber.asLiveData().observe(viewLifecycleOwner) {
            hideMainLoading()
            when (it) {
                is State.Error -> Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                is State.Loading -> {}
                is State.Success -> {
                    val barber = it.data!!
                    binding.tvClientName.text = barber.name
                    binding.tvPhoneNumber.text = barber.phone
                }
            }
        }

        vm.salon.asLiveData().observe(viewLifecycleOwner) {
            hideMainLoading()
            when (it) {
                is State.Error -> Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                is State.Loading -> {}
                is State.Success -> {
                    val salon = it.data!!
                    if (!salon.image.isNullOrBlank())
                        glide().load(salon.image).into(binding.ivSalonPreviewImage)

                }
            }
        }

        vm.reservationState.asLiveData().observe(viewLifecycleOwner) {
            hideMainLoading()
            when (it) {
                is State.Error -> {
                    binding.tvQrMessage.text = it.message
                }
                is State.Loading -> {}
                is State.Success -> {
                    val reservation = it.data!!;
                    binding.reservatonDetails.isVisible = true
                    rvSingleList(binding.rvServices, ItemServicesBinding::inflate, reservation.services ?: emptyList(), LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)) {
                        listBuilder { itemServicesBinding, service ->
                            itemServicesBinding.tvServiceName.text = service.id.title
                        }
                    }

                    binding.tvTotalPrice.text = reservation.services.sumOf { it.price }.toString()
                    binding.tvDate.text = dayNumberFormatter.format(reservation.date ?: Date())
                    binding.tvTime.text = timeFormatter.format(reservation.date ?: Date())
                }
            }


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
            vm.getReservations(it.text)
            requireActivity().runOnUiThread {
                binding.ivReservationIdQr2.setImageBitmap(encodeAsBitmap(it.text))
                scannerView.isVisible = false
            }
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

    fun encodeAsBitmap(str: String?): Bitmap? {
        try {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(str, BarcodeFormat.QR_CODE, 400, 400)
            val w = bitMatrix.width
            val h = bitMatrix.height
            val pixels = IntArray(w * h)
            for (y in 0 until h) {
                for (x in 0 until w) {
                    pixels[y * w + x] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                }
            }
            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h)
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            return null;
        }
    }


}