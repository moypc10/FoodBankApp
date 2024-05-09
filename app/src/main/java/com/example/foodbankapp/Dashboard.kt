package com.example.foodbankapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore

class Dashboard : AppCompatActivity() {
    private lateinit var db:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharePref = this.getPreferences(Context.MODE_PRIVATE)?:return
        val isLogin=sharePref.getString("Email","1")
        setContentView(R.layout.activity_dashboard)

        setText("STEM School")
        with(sharePref.edit())
        {
            putString("Name", "STEM School")
            apply()
        }

        var addDon = findViewById<Button>(R.id.addDonation)
        // Creamos la funcionalidad del back button
        addDon.setOnClickListener{
            var intent = Intent(this, RegisterDonation::class.java)
            startActivity(intent)
            finish()
        }

        var back = findViewById<Button>(R.id.backButton)
        // Creamos la funcionalidad del back button
        back.setOnClickListener{
            var intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun setText(name:String)
    {
        db= FirebaseFirestore.getInstance()
        if(name !=null) {
            db.collection("MATERIAL DONATIONS").document(name).get()
                .addOnSuccessListener { tasks->
                    var name = findViewById<TextView>(R.id.textView16)
                    var phone = findViewById<TextView>(R.id.textView19)
                    var email = findViewById<TextView>(R.id.textView18)
                    var type = findViewById<TextView>(R.id.textView20)


                    name.text=tasks.get("MATERIAL_NAME").toString()
                    phone.text=tasks.get("MATERIAL_PHONE").toString()
                    email.text=tasks.get("MATERIAL_MAIL").toString()
                    type.text=tasks.get("MATERIAL_TYPE").toString()


                }
        }

    }
}



//backButtonDashboard