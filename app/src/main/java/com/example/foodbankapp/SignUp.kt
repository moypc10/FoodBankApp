package com.example.foodbankapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


    class SignUp : AppCompatActivity() {
        lateinit var auth: FirebaseAuth
        lateinit var db: FirebaseFirestore

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_sign_up)

            auth = FirebaseAuth.getInstance()
            db = FirebaseFirestore.getInstance()

            val backButton = findViewById<Button>(R.id.backButton8)

            backButton.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }



            val registerUser = findViewById<Button>(R.id.signUpButton)

            registerUser.setOnClickListener{
                var name_input = findViewById<EditText>(R.id.first_name_input)
                var last_name_input = findViewById<EditText>(R.id.last_name_input)
                var job_type_input = findViewById<EditText>(R.id.job_type)
                var email_input = findViewById<EditText>(R.id.email_Input)
                var password_input = findViewById<EditText>(R.id.password_input)
                //var confirm_passowrd_input = findViewById<EditText>(R.id.confirm_password)

                if (checking()){
                    val userName = name_input.text.toString()
                    val userLname = last_name_input.text.toString()
                    val userJob = job_type_input.text.toString()
                    val userEmail = email_input.text.toString()
                    val passHide = "*"
                    val userPassword =password_input.text.toString()
                    val hiddenPassword = passHide.repeat(password_input.text.length)
                    val user = hashMapOf(
                        "NAME" to userName,
                        "FIRST_LNAME" to userLname,
                        "JOB_TYPE" to userJob,
                        "EMAIL" to userEmail,
                        "PASSWORD" to hiddenPassword,
                    )
                    val userCollection = db.collection("USERS")
                    auth.createUserWithEmailAndPassword(userEmail,userPassword)

                    userCollection.document(userEmail).set(user)
                    Toast.makeText(this, "Resgistro exitoso", Toast.LENGTH_SHORT)
                    val intent= Intent(this,LogInActivity::class.java)
                    startActivity(intent)
                    finish()

                }
                else{

                    Toast.makeText(this, "¡Revisa todos los campos!", Toast.LENGTH_LONG)
                }
            }

        }

        fun checking(): Boolean{
            var userName = findViewById<EditText>(R.id.first_name_input)
            var userLname = findViewById<EditText>(R.id.last_name_input)
            var userJob = findViewById<EditText>(R.id.job_type)
            var userEmail = findViewById<EditText>(R.id.email_Input)
            var userPassword = findViewById<EditText>(R.id.password_input)

            val name = userName.text.toString()
            val lname = userLname.text.toString()
            val job = userJob.text.toString()
            val email = userEmail.text.toString()
            val password = userPassword.text.toString()


            if(name.trim{it<=' '}.isNotEmpty() && lname.trim{it<=' '}.isNotEmpty() && job.trim{it<=' '}.isNotEmpty() && email.trim{it<=' '}.isNotEmpty() && password.trim{it<=' '}.isNotEmpty() && userPassword.text.length>= 8){
                return true
            }

            return false
        }


    }
