package com.demo.saloni.ui.auth.signin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.afollestad.vvalidator.form
import com.demo.saloni.data.remote.AuthServices
import com.demo.saloni.databinding.FragmentSignInBinding
import com.demo.saloni.ui.BaseFragment

private const val TAG = "SignInFragment"
class SignInFragment : BaseFragment() {

    private lateinit var binding: FragmentSignInBinding

    val args: SignInFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        form {
            input(binding.etEmail){
                isNotEmpty()
                isEmail()
            }
            input(binding.etPassword){
                isNotEmpty()
            }

            submitWith(binding.btnLogin){
                if(it.success()){
                    if (args.isSalon)
                        AuthServices().signInSalon(binding.etEmail.text.toString(), binding.etPassword.text.toString(),
                            {
                                Log.e(TAG, "done login salon: "+it )
                            },

                            {
                                Log.e(TAG, "error: "+it )
                            });
                    else
                        AuthServices().signInClient(binding.etEmail.text.toString(), binding.etPassword.text.toString(),
                            {
                                Log.e(TAG, "done login client: "+it )

                            },

                            {
                                Log.e(TAG, "error: "+it )

                            });

                }
            }

        }



        binding.btnSignUp.setOnClickListener {
            findNavController().navigate(
                SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
            )
        }


    }
}