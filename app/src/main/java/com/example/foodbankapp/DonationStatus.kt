package com.example.foodbankapp

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore

class DonationStatus : AppCompatActivity() {

    private var estadoSeleccionado: String = ""
    private lateinit var db:FirebaseFirestore

    private lateinit var dialog: BottomSheetDialog // Declarar el diálogo como una propiedad de la actividad
    private lateinit var backgroundSemiTransparent: FrameLayout
    private var isDialogVisible = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation_status)

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

        val backButton = findViewById<Button>(R.id.backButton5)
        backButton.setOnClickListener {
            // Recupera el valor actualizado del botón statusButton
            val updatedStatus = (findViewById<Button>(R.id.statusButton)).text.toString()
            var intent = Intent(this, HistorialDonations::class.java)
            // Pasa el valor actualizado como extra
            intent.putExtra("updatedStatus", updatedStatus)
            // También pasa el número de folio y el estado de la donación de regreso a HistorialDonations
            intent.putExtra("numFolio", intent.getStringExtra("numFolio"))
            intent.putExtra("idStatus", intent.getStringExtra("idStatus"))
            startActivity(intent)
            finish()
        }


        // Jala el numFolio desde la activity HistorialDonations
        // Recupera el valor de numFolio pasado como extra
        val numFolio = intent.getStringExtra("numFolio")

        // Encuentra el elemento con el ID idFolio en la vista
        val idFolioTextView = findViewById<TextView>(R.id.idFolio)

        // Establece el valor de numFolio en el elemento idFolio
        idFolioTextView.text = numFolio



        val showPopupButton = findViewById<Button>(R.id.statusButton)

        showPopupButton.setOnClickListener {
            showCustomPopup(showPopupButton)
            backgroundSemiTransparent.visibility = View.VISIBLE
        }


        // >>>>>>>>>> LÓGICA PARA LA INFO DEL FOLIO:

        // ============ Aquí se puede implementar el backedn ==============
        // Asignación de valores de ejemplo

        val sharePref = this.getPreferences(Context.MODE_PRIVATE)?:return

        var donationID=intent.getStringExtra("donacionId")

        if(donationID != null)
        {
            setText(donationID)
        }

        // <<<<<<<<<<<<<<

        // Boton back to historial de donaciones:
        var goToHistorialDBTN = findViewById<Button>(R.id.goToHistorialDBTN)

        // Creamos el cambio de activity
        goToHistorialDBTN.setOnClickListener{
            var intent = Intent(this, HistorialDonations::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun showSemiTransparentDialog(dialog: Dialog) {
        backgroundSemiTransparent.visibility = View.VISIBLE

        dialog.setOnDismissListener {
            backgroundSemiTransparent.visibility = View.INVISIBLE
        }

        dialog.show()
    }

    private fun createCompletedStatusDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.completed_status_donation)

        val confirmCompletedButton = dialog.findViewById<Button>(R.id.CompletedDonationButton)

        confirmCompletedButton.setOnClickListener {
            dialog.dismiss()
            showTakePictureDialog()
        }

        showSemiTransparentDialog(dialog)

    }

    private fun showTakePictureDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.agregar_evidencia_pop_up)

        val TomarFotoButton = dialog.findViewById<Button>(R.id.TomarFotoButton)
        val NoEvidenciaButton = dialog.findViewById<Button>(R.id.NoEvidenciaButton)

        TomarFotoButton.setOnClickListener {
            val intent = Intent(this, CreatingPost::class.java)
            startActivity(intent)
            finish()
        }

        NoEvidenciaButton.setOnClickListener {
            dialog.dismiss()
        }

        showSemiTransparentDialog(dialog)
    }

    private fun createCanceledStatusDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.canceled_status_donationa)

        //password_input4

        val confirmCancelationButton = dialog.findViewById<Button>(R.id.TomarFotoButton)

        confirmCancelationButton.setOnClickListener {
            val mes = dialog.findViewById<EditText>(R.id.password_input4)

            // ... (código adicional)
            var donationID=intent.getStringExtra("donacionId")
            val status = hashMapOf<String, Any> ("MESSAGE" to mes.text.toString())

            if (donationID != null) {
                db.collection("DONATION LOG").document(donationID).update(status)
            }
            mes.setText(mes.text)
            var message = findViewById<TextView>(R.id.mensajeTextView)
            message.text=mes.text.toString()

            dialog.dismiss()
        }

        showSemiTransparentDialog(dialog)
    }

    private fun showCustomPopup(buttonToUpdate: Button) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.status_pop_up)

        val activoButton = dialog.findViewById<Button>(R.id.button)
        val completadoButton = dialog.findViewById<Button>(R.id.button2)
        val canceladoButton = dialog.findViewById<Button>(R.id.button3)

        activoButton.setOnClickListener {
            buttonToUpdate.setBackgroundColor(Color.parseColor("#2D8DE5"))
            buttonToUpdate.text = "Activa"
            // ... (código adicional)
            var donationID=intent.getStringExtra("donacionId")
            val status = hashMapOf<String, Any> ("STATUS" to "Activa")

            if (donationID != null) {
                db.collection("DONATION LOG").document(donationID).update(status)
            }
            dialog.setOnDismissListener{
                backgroundSemiTransparent.visibility = View.INVISIBLE
            }

            dialog.dismiss()

        }




        completadoButton.setOnClickListener {
            buttonToUpdate.setBackgroundColor(Color.parseColor("#06CB52"))
            buttonToUpdate.text = "Completada"
            // ... (código adicional)
            var donationID=intent.getStringExtra("donacionId")
            val status = hashMapOf<String, Any> ("STATUS" to "Completada")

            if (donationID != null) {
                db.collection("DONATION LOG").document(donationID).update(status)
            }
            createCompletedStatusDialog()
            dialog.dismiss()
        }


        canceladoButton.setOnClickListener {
            buttonToUpdate.setBackgroundColor(Color.parseColor("#DD0E2A"))
            buttonToUpdate.text = "Cancelada"
            // ... (código adicional)
            var donationID=intent.getStringExtra("donacionId")
            val status = hashMapOf<String, Any> ("STATUS" to "Cancelada")

            if (donationID != null) {
                db.collection("DONATION LOG").document(donationID).update(status)
            }
            createCanceledStatusDialog()
            dialog.dismiss()

        }

        dialog.show()


    }
    private fun setText(donationId:String)
    {
        db= FirebaseFirestore.getInstance()
        if(donationId !=null) {
            db.collection("DONATION LOG").document(donationId).get()
                .addOnSuccessListener { tasks->
                    var folio = findViewById<TextView>(R.id.idFolio)
                    var ally = findViewById<TextView>(R.id.nombreTextView)
                    var allyStatus = findViewById<TextView>(R.id.aliadoTextView)
                    var mail = findViewById<TextView>(R.id.correoTextView)
                    var donation = findViewById<TextView>(R.id.donacionesTextView)
                    var phone = findViewById<TextView>(R.id.telefonoTextView)
                    var message = findViewById<TextView>(R.id.mensajeTextView)
                    var status= findViewById<TextView>(R.id.statusButton)

                    folio.text=tasks.get("FOLIO").toString()
                    ally.text=tasks.get("ALLY").toString()
                    allyStatus.text=tasks.get("ALLY_STATUS").toString()
                    mail.text=tasks.get("MAIL").toString()
                    donation.text=tasks.get("NAME").toString()
                    phone.text=tasks.get("PHONE").toString()
                    message.text=tasks.get("MESSAGE").toString()
                    status.text=tasks.get("STATUS").toString()



                }
        }

    }


}

