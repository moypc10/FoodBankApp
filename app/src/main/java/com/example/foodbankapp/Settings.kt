package com.example.foodbankapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialog

class Settings : AppCompatActivity() {

    private lateinit var dialog: BottomSheetDialog // Declarar el diálogo como una propiedad de la actividad
    private lateinit var backgroundSemiTransparent: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)



        // NABAR: >>>>>>>>

        val dashButton: Button = findViewById(R.id.dashButton)
        val view = layoutInflater.inflate(R.layout.dashboardmenu, null)

        dialog = BottomSheetDialog(this)
        dialog.setContentView(view)

        // Obtén una referencia al fondo semi-transparente
        backgroundSemiTransparent = findViewById(R.id.background_dim)

        dashButton.setOnClickListener {
            // Mostrar el diálogo y cambiar el color del fondo semi-transparente
            backgroundSemiTransparent.visibility = View.VISIBLE
            dialog.show()
        }

        // Asigna un oyente para el evento onDismiss del diálogo
        dialog.setOnDismissListener {
            // Oculta el fondo semi-transparente cuando se cierra el diálogo
            backgroundSemiTransparent.visibility = View.INVISIBLE
        }

        //Obtenemos las referencias de los botones del dashboardmenu
        val mainPage = view.findViewById<Button>(R.id.MainPageButton)
        val recaudacionesButton = view.findViewById<Button>(R.id.recaudacionesButton)
        val donationsHistoryButton = view.findViewById<Button>(R.id.HistoryButton)
        val settingButton = view.findViewById<Button>(R.id.ConfigButton)

        mainPage.setOnClickListener {
            // Realiza las acciones necesarias
            var intent = Intent(this, LoggedInActivity::class.java)
            startActivity(intent)
            finish()
            dialog.dismiss()
        }

        recaudacionesButton.setOnClickListener {
            // Realiza las acciones necesarias
            var intent = Intent(this, Donations::class.java)
            startActivity(intent)
            finish()
            dialog.dismiss()
        }

        donationsHistoryButton.setOnClickListener {
            // Realiza las acciones necesarias
            var intent = Intent(this, HistorialDonations::class.java)
            startActivity(intent)
            finish()
            dialog.dismiss()
        }

        settingButton.setOnClickListener {
            // Realiza las acciones necesarias
            var intent = Intent(this, Settings::class.java)
            startActivity(intent)
            finish()
            dialog.dismiss()
        }

        // <<<<<<<<
    }
}