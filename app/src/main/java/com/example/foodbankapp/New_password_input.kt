package com.example.foodbankapp



import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class New_password_input : AppCompatActivity() {

    lateinit var passwordInput: EditText
    lateinit var confirmPasswordInput: EditText
    lateinit var passwordMatchCondition: TextView
    private lateinit var charactersNumCondition: TextView
    private lateinit var mayusMinCondition: TextView
    private lateinit var specialCharCondition: TextView
    private lateinit var containsNumCondition: TextView
    private lateinit var backButton : Button

    val lightGreenColor = Color.parseColor("#06CB52")
    val defaultColor = Color.parseColor("#FA3C1B")

    //Variables para el toggle de la contraseña
    private lateinit var togglePasswordButton: ImageButton
    private lateinit var togglePasswordButton2: ImageButton
    private var isPasswordVisible = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_password_input)

        passwordInput = findViewById(R.id.password_input)
        confirmPasswordInput = findViewById(R.id.password_input2)
        passwordMatchCondition = findViewById(R.id.condition)
        charactersNumCondition = findViewById(R.id.condition1)
        mayusMinCondition = findViewById(R.id.condition2)
        specialCharCondition = findViewById(R.id.condition3)
        containsNumCondition = findViewById(R.id.condition4)
        backButton = findViewById(R.id.backButton2)


        // Función de backButton
        backButton.setOnClickListener{
            val intent = Intent(this, EditProfile::class.java)
            startActivity(intent)
            finish()
        }


        // Apartado de la visibilidad de la nueva contraseña
        togglePasswordButton = findViewById(R.id.passwordToggle)
        //Función del toggle de la contraseña
        togglePasswordButton.setOnClickListener {
            togglePasswordVisibility()
        }

        // apartado de visibildiad de confirmar contraseña
        togglePasswordButton2 = findViewById(R.id.passwordToggle2)
        togglePasswordButton2.setOnClickListener {
            togglePasswordVisibility2()
        }



        // Configura un TextWatcher para la confirmación de contraseña
        confirmPasswordInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validatePassword()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No es necesario implementar
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No es necesario implementar
            }
        })

        passwordInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validatePassword()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No es necesario implementar
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No es necesario implementar
            }
        })

        val resetPasswordButton = findViewById<Button>(R.id.myprofile)

        resetPasswordButton.setOnClickListener {
            val password = passwordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()

            if (isPasswordValid(password, confirmPassword)) {
                val user = Firebase.auth.currentUser
                user!!.updatePassword(confirmPassword).addOnCompleteListener { task->
                    if(task.isSuccessful){
                        Toast.makeText(
                            this,
                            "Contraseña actualizada con éxito",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(this, LogInActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                    }
                }

                /*
                // modificar el background de acuerdo con el popup
                val backgroundSemiTransparent = findViewById<FrameLayout>(R.id.background_dim)

                // Configurar la visiblidad del background
                backgroundSemiTransparent.visibility = View.VISIBLE

                // Creamos la funcionalidad del custom pop up
                val view = View.inflate(this@New_password_input, R.layout.pop_up_confirm_password, null)
                val builder = AlertDialog.Builder(this@New_password_input)
                builder.setView(view)

                val dialog = builder.create()
                // Función para cuando se cierra el pop up regresar al fondo normal
                dialog.setOnDismissListener{
                    backgroundSemiTransparent.visibility = View.INVISIBLE
                }

                val window = dialog.window
                val layoutParams = window?.attributes
                layoutParams?.gravity = Gravity.CENTER

                //Funcionalidad del botón del pop_up
                val confirmPasswordButton = view.findViewById<Button>(R.id.confirmPasswordButton)

                confirmPasswordButton.setOnClickListener {
                    val intent = Intent(this, LogInActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                dialog.show()

                 */
            } else {
                Toast.makeText(this, "Revisa los datos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Función del toggle de la contraseña
    private fun togglePasswordVisibility(){
        isPasswordVisible = !isPasswordVisible

        //Se cambia el icono
        if(isPasswordVisible){
            togglePasswordButton.setBackgroundResource(R.drawable.open_eye) // Cambiar el icono al ojo abierto campo de texto nueva contraseña
            passwordInput.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        }
        else{
            togglePasswordButton.setBackgroundResource(R.drawable.closedeye) // Cambiar el icono al ojo cerrado campo de texto nueva contraseña
            passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        //mover el cursor al final del texto
        val textLenght = passwordInput.text.length
        passwordInput.setSelection(textLenght)

    }

    private fun togglePasswordVisibility2(){
        isPasswordVisible = !isPasswordVisible

        //Se cambia el icono
        if(isPasswordVisible){
            togglePasswordButton2.setBackgroundResource(R.drawable.open_eye) // Cambiar el icono al ojo abierto campo de texto confirmar contraseña
            confirmPasswordInput.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        }
        else{
            togglePasswordButton2.setBackgroundResource(R.drawable.closedeye) // Cambiar el icono al ojo cerrado campo de texto confirmar contraseña
            confirmPasswordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        //mover el cursor al final del texto
        val textLenght2 = confirmPasswordInput.text.length
        confirmPasswordInput.setSelection(textLenght2)

    }



    fun isPasswordValid(password: String, confirmPassword: String): Boolean {
        val isValidLength = password.length >= 8
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }
        val hasDigit = password.any { it.isDigit() }

        return isValidLength && hasUpperCase && hasLowerCase && hasSpecialChar && hasDigit && password == confirmPassword
    }

    fun validatePassword() {
        val password = passwordInput.text.toString()
        val confirmPassword = confirmPasswordInput.text.toString()

        if (password == confirmPassword) {
            setConditionToLightGreen(passwordMatchCondition)
        } else {
            resetConditionColor(passwordMatchCondition)
        }

        if (password.length >= 8) {
            setConditionToLightGreen(charactersNumCondition)
        } else {
            resetConditionColor(charactersNumCondition)
        }

        if (password.any { it.isUpperCase() } && password.any { it.isLowerCase() }) {
            setConditionToLightGreen(mayusMinCondition)
        } else {
            resetConditionColor(mayusMinCondition)
        }

        if (password.any { !it.isLetterOrDigit() }) {
            setConditionToLightGreen(specialCharCondition)
        } else {
            resetConditionColor(specialCharCondition)
        }

        if (password.any { it.isDigit() }) {
            setConditionToLightGreen(containsNumCondition)
        } else {
            resetConditionColor(containsNumCondition)
        }
    }

    fun setConditionToLightGreen(conditionView: TextView) {
        conditionView.setTextColor(lightGreenColor)
    }

    fun resetConditionColor(conditionView: TextView) {
        conditionView.setTextColor(defaultColor)
    }
}

