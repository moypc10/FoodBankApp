package com.example.foodbankapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
class LoggedInActivity : AppCompatActivity() {

    private lateinit var dialog: BottomSheetDialog
    private lateinit var backgroundSemiTransparent: FrameLayout
    private lateinit var carouselImageView: ImageView
    private val imageResourceIds = intArrayOf(
        R.mipmap.img4,
        R.mipmap.img2,
        R.mipmap.img3
    )
    private var currentImageIndex = 0
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logged_in)

        val dashButton: Button = findViewById(R.id.dashButton)
        val view = layoutInflater.inflate(R.layout.dashboardmenu, null)


        // Tenemos que declarar esta variable para tenerla disponible en la pagina del perfil
        var correo=intent.getStringExtra("email")


        if(correo != null) {

            Global.GlobalVariables.appUserEmail = correo.toString()
        }

        dialog = BottomSheetDialog(this)
        dialog.setContentView(view)

        backgroundSemiTransparent = findViewById(R.id.background_dim)

        dashButton.setOnClickListener {
            backgroundSemiTransparent.visibility = View.VISIBLE
            dialog.show()
        }

        dialog.setOnDismissListener {
            backgroundSemiTransparent.visibility = View.INVISIBLE
        }

        val mainPage = view.findViewById<Button>(R.id.MainPageButton)
        val recaudacionesButton = view.findViewById<Button>(R.id.recaudacionesButton)
        val donationsHistoryButton = view.findViewById<Button>(R.id.HistoryButton)
        val settingButton = view.findViewById<Button>(R.id.ConfigButton)


        //configuración botones principales
        val myprofile = findViewById<Button>(R.id.myprofile)
        val HistoryDonationsButton = findViewById<Button>(R.id.HistoryDonationsButton)
        val donacionesButton = findViewById<Button>(R.id.donacionesButton)
        val createPost = findViewById<Button>(R.id.createPost)

        createPost.setOnClickListener {
            val intent = Intent(this, CreatingPost::class.java)
            startActivity(intent)
            finish()
        }


        myprofile.setOnClickListener {
            val intent = Intent(this, EditProfile::class.java)
            intent.putExtra("email", correo)
            startActivity(intent)
            finish()
        }

        HistoryDonationsButton.setOnClickListener {
            val intent = Intent(this, HistorialDonations::class.java)
            startActivity(intent)
            finish()
        }

        donacionesButton.setOnClickListener {
            val intent = Intent(this, Donations::class.java)
            startActivity(intent)
            finish()
        }

        //configuración botones del menú desplegable
        mainPage.setOnClickListener {
            dialog.dismiss()
        }

        recaudacionesButton.setOnClickListener {
            val intent = Intent(this, Donations::class.java)
            startActivity(intent)
            finish()
            dialog.dismiss()
        }

        donationsHistoryButton.setOnClickListener {
            val intent = Intent(this, HistorialDonations::class.java)
            startActivity(intent)
            finish()
            dialog.dismiss()
        }

        settingButton.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
            finish()
            dialog.dismiss()
        }

        // Configura el carrusel de imágenes
        carouselImageView = findViewById(R.id.imageView1)
        startImageCarousel()
    }

    private fun startImageCarousel() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                carouselImageView.setImageResource(imageResourceIds[currentImageIndex])

                currentImageIndex++
                if (currentImageIndex >= imageResourceIds.size) {
                    currentImageIndex = 0
                }

                handler.postDelayed(this, 4000) // Cambia la imagen cada 3 segundos
            }
        }, 0)
    }
}



