package com.example.foodbankapp

import com.google.firebase.auth.FirebaseUser

class Global {
    object GlobalVariables {
        var appUserEmail: String = ""
        var user: FirebaseUser? = null
    }
}