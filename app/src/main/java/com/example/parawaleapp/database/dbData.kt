package com.example.parawaleapp.database

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

// RealTime Database
val mfirebaseDatabase = FirebaseDatabase.getInstance("https://myparawale-app-default-rtdb.asia-southeast1.firebasedatabase.app/")
val reference = mfirebaseDatabase.reference




// Firebase Storage for Images
val storage = FirebaseStorage.getInstance().reference
val storageReference = storage.child("Images")