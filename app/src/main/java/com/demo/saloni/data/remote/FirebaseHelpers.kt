package com.demo.saloni.data.remote

import android.net.Uri
import androidx.core.net.toFile
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage

object
FirebaseHelpers {

    val storageRef = Firebase.storage.reference


    fun uploadImageFromUri(imageUri: Uri):UploadTask{
        val mountainImagesRef = storageRef.child("profileImages/${imageUri.lastPathSegment}")
        return mountainImagesRef.putFile(imageUri )
    }




}