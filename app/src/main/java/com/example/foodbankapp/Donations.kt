package com.example.foodbankapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore

class Donations : AppCompatActivity() {

    private lateinit var dialog: BottomSheetDialog // Declarar el diálogo como una propiedad de la actividad
    private lateinit var backgroundSemiTransparent: FrameLayout
    private lateinit var db:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donations)

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

        // ******** BUTTONS POPUP MENUS ***************
        // >>>> MONTH MENU:
        // Obtenemos el botón para desplegar los meses:
        val btnMes = findViewById<Button>(R.id.mont_donationms)

        // Create a PopupMenu for months
        val popupMenu = PopupMenu(this, btnMes)

        // Inflate the menu with the month items
        popupMenu.menuInflater.inflate(R.menu.donations_month_menu, popupMenu.menu)

        // Set a click listener for the button to show the Popup Menu MONTHS
        btnMes.setOnClickListener { view: View ->
            popupMenu.show()
        }

        // >>>> SETTINGS MENU:
        // obtenemos el botón para el settings (...):
        val btnSettDonation = findViewById<Button>(R.id.dontations_navbar)

        // Create a PopupMenu for settings
        val settingsPopupMenu = PopupMenu(this, btnSettDonation)

        // Inflate the settings menu
        settingsPopupMenu.menuInflater.inflate(R.menu.donations_settings_menu, settingsPopupMenu.menu)

        // Set a listener for settings menu item clicks SETTINGS
        settingsPopupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            // Handle user selection for settings
            when (menuItem.itemId) {
                R.id.historial_donaciones -> {
                    // Handle the "Historial de donaciones" selection
                    val intent = Intent(this, HistorialDonations::class.java)
                    startActivity(intent)
                    return@setOnMenuItemClickListener true
                }
                else -> return@setOnMenuItemClickListener false
            }
        }

        // Set a click listener for the button to show the Popup Menu SETTINGS
        btnSettDonation.setOnClickListener { view: View ->
            settingsPopupMenu.show()
        }


        // ************ Progress bars **************

        // NOTA MUY IMPORTANTE: Desarrollé el front con la idea (money, meds y food)
        // Pero después de la última junta con Brenda (FoodBank) decidió que cambiaramos
        // de Money --> Clothes (ropa), sin embargo tod_o lo tengo implementado con
        // la palabra MONEY, pero para avisarles que siemore que vean money ahora es ropa
        // SOLAMENTE en el UI si lo cambié para que en la screen diga ropa :)

        // >>>>>>>>> MONEY:
        // Obtener referencia al ProgressBar
        val progressBarMoney = findViewById<ProgressBar>(R.id.progressBarMoney)
        // Obtenemos en variables los valores de los números de progreso y objetivo
        val progressTextMoney = findViewById<TextView>(R.id.textViewDonationMoney)
        val goalTextMoney = findViewById<TextView>(R.id.textViewGoalMoney)

        // >>>>>>>>>> MEDS:

        // Obtener referencia al ProgressBar
        val progressBarMeds = findViewById<ProgressBar>(R.id.progressBarMeds)
        // Obtenemos en variables los valores de los números de progreso y objetivo
        val progressTextMeds = findViewById<TextView>(R.id.textViewDonationMeds)
        val goalTextMeds = findViewById<TextView>(R.id.textViewGoalMeds)

        // >>>>>>>>>>> FOOD:
        // Obtener referencia al ProgressBar
        val progressBarFood = findViewById<ProgressBar>(R.id.progressBarFood)
        // Obtenemos en variables los valores de los números de progreso y objetivo
        val progressTextFood = findViewById<TextView>(R.id.textViewDonationFood)
        val goalTextFood = findViewById<TextView>(R.id.textViewGoalFood)

        //********* de manera optimizada
        // Define una clase para representar los datos del mes
        data class MonthData(val currentProgressMoney: Int, val goalMoney: Int,
                             val currentProgressMeds: Int, val goalMeds: Int,
                             val currentProgressFood: Int, val goalFood: Int)




        // DICCIONARIO DE VARIABLES PARA BACK-END (SAM, ANA)!!
        var monthData = mutableMapOf(
            "incializador" to MonthData(55, 100, 23, 100, 40, 100)

        )

        db= FirebaseFirestore.getInstance()
        db.collection("MONTHLY DONATIONS").get()
            .addOnSuccessListener { result->

                for (document in result) {

                    monthData+= mapOf (document.data["name"].toString() to MonthData(document.data["clothesProgress"].toString().toInt(),
                                                document.data["clothesGoal"].toString().toInt(),
                                                document.data["medProgress"].toString().toInt(),
                                                document.data["medGoal"].toString().toInt(),
                                                document.data["foodProgress"].toString().toInt(),
                                                document.data["foodGoal"].toString().toInt()))

                    Log.d("MESSS" , document.data["name"].toString())
                }


                // Función para actualizar el emoji dependiendo de si se alcanza el objetivo
                fun updateEmoji(progressBar: ProgressBar, goal: Int, emojiViewId: Int) {
                    val emojiImageView = findViewById<ImageView>(emojiViewId)

                    if (progressBar.progress >= goal) {
                        emojiImageView.visibility = View.VISIBLE // Mostrar el emoji si se alcanza el objetivo
                        emojiImageView.setImageResource(R.drawable.check) // Cambia el emoji de éxito
                    } else {
                        emojiImageView.visibility = View.INVISIBLE // Ocultar el emoji si no se alcanza el objetivo
                    }
                }

                // función para tomar los valores de progress, goal para después mostrarlos
                fun updateProgressBars(selectedMonthData: MonthData) {
                    // Actualizar ProgressBar de Money (now clothes)
                    progressBarMoney.max = selectedMonthData.goalMoney
                    progressBarMeds.max = selectedMonthData.goalMeds
                    progressBarFood.max = selectedMonthData.goalFood
                    progressBarMoney.progress = selectedMonthData.currentProgressMoney
                    progressTextMoney.text = "${selectedMonthData.currentProgressMoney} Pzs"
                    goalTextMoney.text = "Objetivo: ${selectedMonthData.goalMoney} Pzs"
                    updateEmoji(progressBarMoney, selectedMonthData.goalMoney, R.id.moneyEmoji) // Llamada a la función para actualizar el emoji

                    // Actualizar ProgressBar de Meds
                    progressBarMeds.progress = selectedMonthData.currentProgressMeds
                    progressTextMeds.text = "${selectedMonthData.currentProgressMeds} Pzs"
                    goalTextMeds.text = "Objetivo: ${selectedMonthData.goalMeds} Pzs"
                    updateEmoji(progressBarMeds, selectedMonthData.goalMeds, R.id.medsEmoji)

                    // Actualizar ProgressBar de Food
                    progressBarFood.progress = selectedMonthData.currentProgressFood
                    progressTextFood.text = "${selectedMonthData.currentProgressFood} Kg"
                    goalTextFood.text = "Objetivo: ${selectedMonthData.goalFood} Kg"
                    updateEmoji(progressBarFood, selectedMonthData.goalFood, R.id.foodEmoji)
                }

                // >>>>>>> ANIMATION progress bar
                // clase progress bar animation
                class ProgressBarAnimation(
                    private val progressBar: ProgressBar,
                    private val from: Int,
                    private val to: Int
                ) : Animation() {
                    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                        val value = from + (to - from) * interpolatedTime
                        progressBar.progress = value.toInt()
                    }
                }

                // Función para animar la progress-bar
                fun animateProgressBar(progressBar: ProgressBar, targetProgress: Int) {
                    val animation = ProgressBarAnimation(progressBar, progressBar.progress, targetProgress)
                    animation.duration = 1000 // Duración de la animación en milisegundos (ajusta según tus preferencias)
                    progressBar.startAnimation(animation)
                }

                // obtener el text del mes para cambiar al nombre del mes seleccionado
                val btnMesText = findViewById<TextView>(R.id.textMesDonacion)

                // LISTENER
                popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->

                    when (menuItem.itemId) {
                        R.id.enero -> {
                            val selectedMonthName = menuItem.title.toString()
                            btnMesText.text = selectedMonthName
                            val selectedMonthData = monthData["Enero"]
                            if (selectedMonthData != null) {
                                animateProgressBar(progressBarMoney, selectedMonthData.currentProgressMoney)
                                animateProgressBar(progressBarMeds, selectedMonthData.currentProgressMeds)
                                animateProgressBar(progressBarFood, selectedMonthData.currentProgressFood)
                                updateProgressBars(selectedMonthData)
                            }
                            return@setOnMenuItemClickListener true
                        }R.id.febrero -> {
                        val selectedMonthName = menuItem.title.toString()
                        btnMesText.text = selectedMonthName
                        val selectedMonthData = monthData["Febrero"]
                        if (selectedMonthData != null) {
                            animateProgressBar(progressBarMoney, selectedMonthData.currentProgressMoney)
                            animateProgressBar(progressBarMeds, selectedMonthData.currentProgressMeds)
                            animateProgressBar(progressBarFood, selectedMonthData.currentProgressFood)
                            updateProgressBars(selectedMonthData)
                        }
                        return@setOnMenuItemClickListener true
                    }R.id.marzo -> {
                        val selectedMonthName = menuItem.title.toString()
                        btnMesText.text = selectedMonthName
                        val selectedMonthData = monthData["Marzo"]
                        if (selectedMonthData != null) {
                            animateProgressBar(progressBarMoney, selectedMonthData.currentProgressMoney)
                            animateProgressBar(progressBarMeds, selectedMonthData.currentProgressMeds)
                            animateProgressBar(progressBarFood, selectedMonthData.currentProgressFood)
                            updateProgressBars(selectedMonthData)
                        }
                        return@setOnMenuItemClickListener true
                    }R.id.abril -> {
                        val selectedMonthName = menuItem.title.toString()
                        btnMesText.text = selectedMonthName
                        val selectedMonthData = monthData["Abril"]
                        if (selectedMonthData != null) {
                            animateProgressBar(progressBarMoney, selectedMonthData.currentProgressMoney)
                            animateProgressBar(progressBarMeds, selectedMonthData.currentProgressMeds)
                            animateProgressBar(progressBarFood, selectedMonthData.currentProgressFood)
                            updateProgressBars(selectedMonthData)
                        }
                        return@setOnMenuItemClickListener true
                    }R.id.mayo -> {
                        val selectedMonthName = menuItem.title.toString()
                        btnMesText.text = selectedMonthName
                        val selectedMonthData = monthData["Mayo"]
                        if (selectedMonthData != null) {
                            animateProgressBar(progressBarMoney, selectedMonthData.currentProgressMoney)
                            animateProgressBar(progressBarMeds, selectedMonthData.currentProgressMeds)
                            animateProgressBar(progressBarFood, selectedMonthData.currentProgressFood)
                            updateProgressBars(selectedMonthData)
                        }
                        return@setOnMenuItemClickListener true
                    }R.id.junio -> {
                        val selectedMonthName = menuItem.title.toString()
                        btnMesText.text = selectedMonthName
                        val selectedMonthData = monthData["Junio"]
                        if (selectedMonthData != null) {
                            animateProgressBar(progressBarMoney, selectedMonthData.currentProgressMoney)
                            animateProgressBar(progressBarMeds, selectedMonthData.currentProgressMeds)
                            animateProgressBar(progressBarFood, selectedMonthData.currentProgressFood)
                            updateProgressBars(selectedMonthData)
                        }
                        return@setOnMenuItemClickListener true
                    }R.id.julio -> {
                        val selectedMonthName = menuItem.title.toString()
                        btnMesText.text = selectedMonthName
                        val selectedMonthData = monthData["Julio"]
                        if (selectedMonthData != null) {
                            animateProgressBar(progressBarMoney, selectedMonthData.currentProgressMoney)
                            animateProgressBar(progressBarMeds, selectedMonthData.currentProgressMeds)
                            animateProgressBar(progressBarFood, selectedMonthData.currentProgressFood)
                            updateProgressBars(selectedMonthData)
                        }
                        return@setOnMenuItemClickListener true
                    }R.id.agosto -> {
                        val selectedMonthName = menuItem.title.toString()
                        btnMesText.text = selectedMonthName
                        val selectedMonthData = monthData["Agosto"]
                        if (selectedMonthData != null) {
                            animateProgressBar(progressBarMoney, selectedMonthData.currentProgressMoney)
                            animateProgressBar(progressBarMeds, selectedMonthData.currentProgressMeds)
                            animateProgressBar(progressBarFood, selectedMonthData.currentProgressFood)
                            updateProgressBars(selectedMonthData)
                        }
                        return@setOnMenuItemClickListener true
                    }R.id.septiembre -> {
                        val selectedMonthName = menuItem.title.toString()
                        btnMesText.text = selectedMonthName
                        val selectedMonthData = monthData["Septiembre"]
                        if (selectedMonthData != null) {
                            animateProgressBar(progressBarMoney, selectedMonthData.currentProgressMoney)
                            animateProgressBar(progressBarMeds, selectedMonthData.currentProgressMeds)
                            animateProgressBar(progressBarFood, selectedMonthData.currentProgressFood)
                            updateProgressBars(selectedMonthData)
                        }
                        return@setOnMenuItemClickListener true
                    }R.id.octubre -> {
                        val selectedMonthName = menuItem.title.toString()
                        btnMesText.text = selectedMonthName
                        val selectedMonthData = monthData["Octubre"]
                        if (selectedMonthData != null) {
                            animateProgressBar(progressBarMoney, selectedMonthData.currentProgressMoney)
                            animateProgressBar(progressBarMeds, selectedMonthData.currentProgressMeds)
                            animateProgressBar(progressBarFood, selectedMonthData.currentProgressFood)
                            updateProgressBars(selectedMonthData)
                        }
                        return@setOnMenuItemClickListener true
                    }R.id.noviembre -> {
                        val selectedMonthName = menuItem.title.toString()
                        btnMesText.text = selectedMonthName
                        val selectedMonthData = monthData["Noviembre"]
                        if (selectedMonthData != null) {
                            animateProgressBar(progressBarMoney, selectedMonthData.currentProgressMoney)
                            animateProgressBar(progressBarMeds, selectedMonthData.currentProgressMeds)
                            animateProgressBar(progressBarFood, selectedMonthData.currentProgressFood)
                            updateProgressBars(selectedMonthData)
                        }
                        return@setOnMenuItemClickListener true
                    }R.id.diciembre -> {
                        val selectedMonthName = menuItem.title.toString()
                        btnMesText.text = selectedMonthName
                        val selectedMonthData = monthData["Diciembre"]
                        if (selectedMonthData != null) {
                            animateProgressBar(progressBarMoney, selectedMonthData.currentProgressMoney)
                            animateProgressBar(progressBarMeds, selectedMonthData.currentProgressMeds)
                            animateProgressBar(progressBarFood, selectedMonthData.currentProgressFood)
                            updateProgressBars(selectedMonthData)
                        }
                        return@setOnMenuItemClickListener true
                    }
                        else -> return@setOnMenuItemClickListener false
                    }
                }
            }



    }
}