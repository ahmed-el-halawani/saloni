package com.demo.barber.ui.salonqr

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ScanMode
import com.demo.barber.R
import com.demo.barber.data.remote.entities.PaymentMethods
import com.demo.barber.databinding.FragmentSalonScanQrBinding
import com.demo.barber.databinding.ItemServicesBinding
import com.demo.barber.ui.core.*
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

    var requestPermissionLauncher: ActivityResultLauncher<String>? = null;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setBackButton(binding.btnBack)
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                initAction()
            } else {
                hideMainLoading()
                findNavController().popBackStack()
                Toast.makeText(context, "cant read qr without permission", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            initAction()
        } else {
            requestPermissionLauncher?.launch(Manifest.permission.CAMERA)
        }


    }

    private fun initAction() {
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
                    if (!barber.image.isNullOrBlank()) {
                        firebaseGlide(barber.image!!, binding.ivBarberImage2)
                        firebaseGlide(barber.image!!, binding.ivBarberImage)
                    }
                    binding.tvBarberName.text = barber.name

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
                        firebaseGlide(salon.image!!, binding.ivSalonPreviewImage)

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

                    binding.tvTotalPrice.text = reservation.services.sumOf { it.price }.toMoney()
                    binding.tvDate.text = dayNumberFormatter.format(reservation.date ?: Date())
                    binding.tvTime.text = timeFormatter.format(reservation.date ?: Date())


                    when (reservation.paymentMethod) {
                        PaymentMethods.Cash -> {
                            binding.paymentContainer.setBackgroundColor(resources.getColor(R.color.white))
                            binding.tvTotalPrice.setTextColor(resources.getColor(R.color.black))
                        }
                        PaymentMethods.Kent -> {
                            binding.tvCash.isVisible = false
                            binding.kent.isVisible = true
                        }
                    }
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