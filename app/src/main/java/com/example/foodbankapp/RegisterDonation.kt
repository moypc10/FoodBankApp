package com.example.foodbankapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterDonation : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_donation)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        var backButton = findViewById<Button>(R.id.backButton)
        // Creamos la funcionalidad del back button
        backButton.setOnClickListener{
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val registerDonation = findViewById<Button>(R.id.donateButton)

        registerDonation.setOnClickListener{
            var NameDonationInput = findViewById<EditText>(R.id.name_donations)
            var TypeDonation = findViewById<EditText>(R.id.Tipo_donación)
            var Donation_Phone = findViewById<EditText>(R.id.phone_number)
            var Email_Donation = findViewById<EditText>(R.id.email_Input)



            if (checking()){
                val DonName = NameDonationInput.text.toString()
                val DonType = TypeDonation.text.toString()
                val DonPhone = Donation_Phone.text.toString()
                val DonEmail = Email_Donation.text.toString()


                val donation = hashMapOf(
                    "MATERIAL_NAME" to DonName,
                    "MATERIAL_TYPE" to DonType,
                    "MATERIAL_PHONE" to DonPhone,
                    "MATERIAL_MAIL" to DonEmail
                )
                Log.d("AQUIII" , DonName)
                val DonationCollection = db.collection("MATERIAL DONATIONS")

                DonationCollection.document(DonName).set(donation)
                Toast.makeText(this, "Registered!", Toast.LENGTH_SHORT)
                val intent= Intent(this,Dashboard::class.java)
                startActivity(intent)
                finish()

            }
            else{
                Toast.makeText(this, "Verify all the spaces are filled in!", Toast.LENGTH_SHORT)
            }
        }

    }

    private fun checking(): Boolean{
        var donationNameInput = findViewById<EditText>(R.id.name_donations)
        var donationTypeInput = findViewById<EditText>(R.id.Tipo_donación)
        var donationPhoneInput = findViewById<EditText>(R.id.phone_number)
        var donationMailInput = findViewById<EditText>(R.id.email_Input)


        val name = donationNameInput.text.toString()
        val type = donationTypeInput.text.toString()
        val phone = donationPhoneInput.text.toString()
        val email = donationMailInput.text.toString()


        if(name.trim{it<=' '}.isNotEmpty() && type.trim{it<=' '}.isNotEmpty() && phone.trim{it<=' '}.isNotEmpty() && email.trim{it<=' '}.isNotEmpty() ){
            return true
        }
        return false
    }
}

