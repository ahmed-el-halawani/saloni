package com.demo.barber.ui.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.afollestad.vvalidator.form
import com.demo.barber.data.remote.entities.Reservation
import com.demo.barber.databinding.FragmentPaymentBinding
import com.demo.barber.ui.barberpreview.BarberServiceViewModel
import com.demo.barber.ui.core.BaseFragment
import com.demo.barber.ui.core.toMoney

class FragmentPayment : BaseFragment() {
    val binding: FragmentPaymentBinding by lazy {
        FragmentPaymentBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setBackButton(binding.btnBack)
        return binding.root
    }

    private val listOfCardNumbers = listOf(
        Card("4263982640269299", "04", "2023", "123"),
        Card("6362970000457013", "08", "2023", "345"),
        Card("5011054488597827", "12", "2023", "567"),
    )

    val vm: BarberServiceViewModel by viewModels()

    val args: FragmentPaymentArgs by navArgs()

    val reservation: Reservation by lazy {
        args.reservation
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTotalAmount.text = reservation.services.sumOf { it.price }.toMoney()

        form {
            input(binding.etCardNumber) {
                isNotEmpty()
                length().greaterThan(15).lessThan(21)
            }
            input(binding.etPin) {
                isNotEmpty()
                this.length().exactly(3)
            }
            input(binding.etMonth) {
                isNotEmpty()
                this.length().exactly(2)
            }
            input(binding.etYear) {
                isNotEmpty()
                this.length().exactly(4)
            }

            submitWith(binding.btnConfirm) {
                if (it.success()) {
                    binding.apply {
                        val card = Card(etCardNumber.text.toString(), etMonth.text.toString(), etYear.text.toString(), etPin.text.toString())

                        if (listOfCardNumbers.contains(card))
                            vm.addReservation(reservation.barberId, reservation.salonId, reservation.services, reservation.paymentMethod).asLiveData()
                                .observe(viewLifecycleOwner) {
                                    Toast.makeText(context, "do reservation", Toast.LENGTH_SHORT).show()
                                    findNavController().navigate(
                                        FragmentPaymentDirections.actionFragmentPayment2ToPaymentDone()
                                    )
                                }
                        else
                            Toast.makeText(context, "Your card not supported yet", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }

    }


}