package com.example.foodbankapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore

class EditProfile : AppCompatActivity() {

    private lateinit var imageEditProfile: ImageView
    private lateinit var db:FirebaseFirestore
    private lateinit var dialog: BottomSheetDialog // Declarar el diálogo como una propiedad de la actividad
    private lateinit var backgroundSemiTransparent: FrameLayout

    private val pickImageLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val selectedImageUri = data.data
                    if (selectedImageUri != null) {
                        imageEditProfile.setImageURI(selectedImageUri)
                    }
                }
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

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



        val editButton = findViewById<Button>(R.id.EditButton)
        val editPasswordButton = findViewById<Button>(R.id.EditPasswordButton)
        imageEditProfile = findViewById<ImageView>(R.id.ImageEditProfile)

        val sharePref = this.getPreferences(Context.MODE_PRIVATE)?:return
        var correo=intent.getStringExtra("email")

        if (Global.GlobalVariables.appUserEmail.toString() != null) {
            setText(Global.GlobalVariables.appUserEmail.toString())
        }

        editPasswordButton.setOnClickListener {
            val intent = Intent(this, New_password_input::class.java)
            startActivity(intent)
            finish()
        }

        editButton.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.menuInflater.inflate(R.menu.upload_pic_options_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_gallery -> {
                        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        pickImageLauncher.launch(intent)
                        true
                    }
                    R.id.menu_camera -> {
                        if (checkCameraPermission()) {
                            openCamera()
                        } else {
                            requestCameraPermission()
                        }
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_IMAGE_CAPTURE)
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                imageEditProfile.setImageBitmap(imageBitmap)
            }
        }
    }


    private fun setText(correo:String)
    {
        db= FirebaseFirestore.getInstance()
        if(correo !=null) {
            db.collection("USERS").document(correo).get()
                .addOnSuccessListener { tasks->
                    var name = findViewById<TextView>(R.id.NameEditProfile)
                    var lastname = findViewById<TextView>(R.id.LastNameEditProfile)
                    var userpassword = findViewById<TextView>(R.id.textView21)
                    var mail = findViewById<TextView>(R.id.EmailEditProfile)

                    mail.text=correo
                    name.text=tasks.get("NAME").toString()
                    lastname.text=tasks.get("FIRST_LNAME").toString()
                    userpassword.text=tasks.get("PASSWORD").toString()

                }
        }

    }
    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
    }
}