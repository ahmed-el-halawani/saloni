package com.demo.saloni.data.remote

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Api {

    companion object{
        private var instance:Api? = null;

        public fun getInstance():Api{
            if(instance == null)
                instance = Api();
            return instance!!;
        }
    }

    private var database: DatabaseReference = Firebase.database.reference
    private var auth = Firebase.auth



}