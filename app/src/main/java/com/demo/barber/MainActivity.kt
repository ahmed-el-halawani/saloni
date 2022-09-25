package com.demo.barber

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.demo.barber.data.local.CashedData
import com.demo.barber.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        CashedData.app = this.application
        setContentView(binding.root)

        Firebase.database.reference.child("isWorking").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isWorking = snapshot.getValue(Boolean::class.java)
                if (isWorking != null && !isWorking) {
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    fun getMainLoading(): View {
        return binding.loadingView
    }
}