package com.demo.saloni.ui.payment

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.demo.saloni.R
import com.demo.saloni.databinding.FragmentPaymentBinding
import com.demo.saloni.databinding.FragmentPaymentDoneBinding
import com.demo.saloni.databinding.ItemServicesBinding
import com.demo.saloni.ui.core.BaseFragment
import com.demo.saloni.ui.core.State
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.newcore.easyrecyclergenerator.rvSingleList
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Flow

class PaymentDone : BaseFragment() {

    private val binding by lazy {
        FragmentPaymentDoneBinding.inflate(layoutInflater)
    }

    private val vm: PaymentDoneViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setBackButton(binding.btnBack)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.ivReservationIdQr.setOnClickListener {
            findNavController().popBackStack()
        }

        vm.barber.asLiveData().observe(viewLifecycleOwner) {
            hideMainLoading()
            when (it) {
                is State.Error -> Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                is State.Loading -> showMainLoading()
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
                is State.Loading -> showMainLoading()
                is State.Success -> {
                    val salon = it.data!!
                    if (!salon.image.isNullOrBlank())
                        Glide.with(requireContext()).load(salon.image).into(binding.ivSalonPreviewImage)

                }
            }
        }

        val dayNumberFormatter = SimpleDateFormat("dd MMMM,yyyy")
        val timeFormatter = SimpleDateFormat("h:mm a")
        vm.getReservation().asLiveData().observe(viewLifecycleOwner) {
            hideMainLoading()
            when (it) {
                is State.Error -> {
                    if (it.message == "reservation not found") {
                        binding.reservationData.isVisible = false
                        binding.tvNoReservations.isVisible = true
                    } else {
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is State.Loading -> showMainLoading()
                is State.Success -> {
                    val reservation = it.data!!;
                    binding.reservationData.isVisible = true
                    binding.tvNoReservations.isVisible = false
                    binding.btnRemoveReservation.setOnClickListener {
                        vm.cancelReservation(reservation.reservationId).asLiveData().observe(viewLifecycleOwner) { cancelState ->
                            hideMainLoading()
                            when (cancelState) {
                                is State.Error -> Toast.makeText(context, cancelState.message, Toast.LENGTH_SHORT).show()
                                is State.Loading -> showMainLoading()
                                is State.Success -> {
                                    binding.reservationData.isVisible = false
                                    binding.tvNoReservations.isVisible = true
                                }
                            }
                        }
                    }
                    binding.ivReservationIdQr.setImageBitmap(encodeAsBitmap(reservation.reservationId))
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