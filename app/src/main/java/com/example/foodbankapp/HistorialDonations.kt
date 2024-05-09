package com.example.foodbankapp

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await


class HistorialDonations : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dialog: BottomSheetDialog // Declarar el diálogo como una propiedad de la actividad
    private lateinit var backgroundSemiTransparent: FrameLayout

    private lateinit var db:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_donations)


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

        val scrollView = findViewById<LinearLayout>(R.id.linearLayoutInScrollView)
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)


        // we call firestore to retrieve the information
        db= FirebaseFirestore.getInstance()
        val list = ArrayList<Donacion>()
        val selectedStatus = sharedPreferences.getString("selectedStatus", "Activo") ?: "Activo"

        var donationPosition: Int = -1
        val someActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val estadoActualizado = data?.getStringExtra("idStatus")
                val donacionId = data?.getIntExtra("donacionId", -1)
                if (estadoActualizado != null && donacionId != -1) {
                    // Actualiza el estado en la lista elementosDeDonacion
                    for (donacion in list) {
                        if (donacion.id == donacionId) {
                            donacion.estado = estadoActualizado
                            break
                        }
                    }
                }
            }
        }

        db.collection("DONATION LOG").get()
            .addOnSuccessListener { result->

                for (document in result) {

                    // Infla el diseño del elemento de donación
                    val itemView = LayoutInflater.from(this).inflate(R.layout.recylcler_view_item, null, false)

                    val numFolioTextView = itemView.findViewById<TextView>(R.id.NumFolio)
                    numFolioTextView.text = "FOLIO " + document.data["FOLIO"].toString()

                    val idstatusTextView = itemView.findViewById<TextView>(R.id.idstatus)
                    idstatusTextView.text = document.data["STATUS"].toString()

                    val idNombreTextView = itemView.findViewById<TextView>(R.id.idNombre)
                    idNombreTextView.text = document.data["NAME"].toString()

                    val aliadoStatusTextView = itemView.findViewById<TextView>(R.id.aliadoStatus)
                    aliadoStatusTextView.text = document.data["ALLY"].toString()


                    val folioButton = itemView.findViewById<Button>(R.id.FolioButton)
                    val asignarChoferDialogButton = itemView.findViewById<Button>(R.id.AsignarButton)
                    val dialog = Dialog(this)
                    dialog.setContentView(R.layout.chofer_status)
                    val AsignarChoferDialog = dialog.findViewById<Button>(R.id.asignarChoferButton2)
                    val DesasignarChoferButton = dialog.findViewById<Button>(R.id.DesasignarChoferButton)

                    val chofer = dialog.findViewById<EditText>(R.id.password_input4)
                    chofer.setText(document.data["DRIVER"].toString())

                    var isAsignado = false // Inicialmente, no asignado

                    if (document.data["DRIVER"].toString() != null && document.data["DRIVER"].toString() != "")
                    {
                        isAsignado = true
                        asignarChoferDialogButton.text = "Asignado"
                        asignarChoferDialogButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.lightBlue))
                    }
                    else
                    {
                        asignarChoferDialogButton.text = "Asignar"
                        asignarChoferDialogButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.lightGreen))
                    }

                    asignarChoferDialogButton.setOnClickListener {
                        backgroundSemiTransparent.visibility = View.VISIBLE

                        dialog.show()
                    }


                    // CAMBIAR BOTÓN CUANDO IS FALSE
                    DesasignarChoferButton.setOnClickListener {

                        isAsignado = false

                        // backend para eliminar el chofer
                        updateDriver(document.id, "")

                        // Restablece el EditText a un valor vacío para que no se vea ningun chofer
                        chofer.text.clear()

                        asignarChoferDialogButton.text = "Asignar"
                        asignarChoferDialogButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.lightGreen))

                        dialog.dismiss()
                    }

                    // CAMBIAR BOTÓN CUANDO IS TRUE
                    AsignarChoferDialog.setOnClickListener {
                        // Realiza la lógica de backend para guardar el nombre del chofer
                        updateDriver(document.id, chofer.text.toString())

                        chofer.setText(chofer.text)

                        // Cambia el estado del botón a asignado
                        isAsignado = true

                        // Cambia el texto del botón a "Asignado"
                        asignarChoferDialogButton.text = "Asignado"
                        asignarChoferDialogButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.lightBlue))

                        dialog.dismiss()
                    }

                    dialog.setOnDismissListener {
                        // Oculta el fondo semitransparente cuando se cierra el diálogo
                        backgroundSemiTransparent.visibility = View.INVISIBLE
                    }

                    val layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    layoutParams.setMargins(0, 0, 0, 24)
                    itemView.layoutParams = layoutParams
                    scrollView.addView(itemView)

                    folioButton.setOnClickListener {
                        val intentFolio = Intent(this, DonationStatus::class.java)
                        intentFolio.putExtra("idStatus", document.data["STATUS"].toString())
                        intentFolio.putExtra("donacionId", document.id.toString()) // Pasar el identificador único
                        startActivity(intentFolio)
                        //donationPosition = 1
                        //someActivityResultLauncher.launch(intent)

                    }
                }
            }
    }

    fun updateDriver(documentId: String, newValue: Any){
        val driver = hashMapOf<String, Any> ("DRIVER" to newValue)

        db.collection("DONATION LOG").document(documentId).update(driver)
            .addOnSuccessListener {
                Log.d(documentId,newValue.toString())
            }
    }



    data class Donacion(val id: Int, val numFolio: String, val idStatus: String, val idNombre: String, val aliadoStatus: String, var estado: String)
}

