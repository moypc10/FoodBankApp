package com.example.foodbankapp

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase

class LogInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        auth=FirebaseAuth.getInstance()

        // Obtenemos nuestro botón de LogIn por id
        var loginButton = findViewById<Button>(R.id.LogInButton)

        //Obtenemos nuestro email input de nuestro UI
        var emailInput = findViewById<EditText>(R.id.email_Input)
        var passwordInput = findViewById<EditText>(R.id.password_Input)

        // Definimos variable y creamos la funcionaldiad de reset password
        var resetPassword = findViewById<TextView>(R.id.Restablecer_password)
        // Creamos la función para el cmabio de activity para la activity de reestablecer la contraseña

        resetPassword.setOnClickListener{
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.forgot_password_popup)
            dialog.show()

            val emailResetButton = dialog.findViewById<Button>(R.id.email_reset_button)

            emailResetButton.setOnClickListener {
                val resetEmailAddress = dialog.findViewById<EditText>(R.id.email_input_2).text.toString()
                if(resetEmailAddress.trim{it<=' '}.isNotEmpty()) {
                    auth.setLanguageCode("es")
                    auth.sendPasswordResetEmail(resetEmailAddress).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this,
                                "Correo de recuperación enviado.",
                                Toast.LENGTH_LONG
                            ).show()
                            dialog.dismiss()
                        } else {
                            Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                            dialog.dismiss()
                        }
                    }
                }
                else{
                    Toast.makeText(this, "Favor de ingresar tu correo.", Toast.LENGTH_LONG).show()
                }
            }
        }

        var backButton = findViewById<Button>(R.id.backButton)
        // Creamos la función para el cambio de activity para la anterior con el back button
        backButton.setOnClickListener{
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


        //Creamos la funcionalidad del botón para el cambio de activity y la programación con la base de datos
         loginButton.setOnClickListener{

            if (checking())
            {
                // convertirmos nuestras entrads de texto en string estas son las variables que se van a utilizar para llamar a la función de
                // "auth.signInWithEmailAndPassword(email,password)"
                val email = emailInput.text.toString()
                val password = passwordInput.text.toString()

                auth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this){
                        task->
                        if(task.isSuccessful)
                        {
                           val user: FirebaseUser? = auth.currentUser
                            if (user!= null)
                            {
                                Global.GlobalVariables.user = user
                            }
                        // Aquí es donde si el inicio de sesión fue exitoso, se cambia a la activity de logged In
                            var intent = Intent(this, LoggedInActivity::class.java)
                            intent.putExtra("email", email)
                            startActivity(intent)
                            Toast.makeText(this, "Login Successful!", Toast.LENGTH_LONG).show()
                        }
                        else
                        {
                            Toast.makeText(this, "Check your credentials!", Toast.LENGTH_LONG).show()

                        }
                    }

            }
            else
            {
                Toast.makeText(this, "¡Completa todos los campos!", Toast.LENGTH_LONG).show()
            }
        }



    }

    // Creamos nuestra función para comprobar si se trata de datos vacios o no
    private fun checking():Boolean
    {
        var emailInput = findViewById<EditText>(R.id.email_Input)
        var passwordInput = findViewById<EditText>(R.id.password_Input)

        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()

        if(email.trim{it<=' '}.isNotEmpty() && password.trim{it<=' '}.isNotEmpty())
        {
            return true
        }
        return false
    }
}
