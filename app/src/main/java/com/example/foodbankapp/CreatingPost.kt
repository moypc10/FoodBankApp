package com.example.foodbankapp

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.facebook.share.widget.ShareDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.SharePhotoContent

class CreatingPost : AppCompatActivity() {

    // REQUEST image de manifest
    private lateinit var imageLauncher: ActivityResultLauncher<Intent>
    private lateinit var imageToUpload: ImageView
    private val REQUEST_IMAGE_CAPTURE = 1

    private lateinit var dialog: BottomSheetDialog // Declarar el diálogo como una propiedad de la actividad
    private lateinit var backgroundSemiTransparent: FrameLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creating_post)

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


        // <<<<<<<<<<<<<<<<<<<<<<<<<<<

        // OBTENER referencia id a image y btn upload pic
        imageToUpload = findViewById(R.id.imageToUpload)
        val btnUploadPic = findViewById<Button>(R.id.btnUploadPic)

        // Subir foto desde galería
        imageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri = result.data?.data
                if (selectedImageUri != null) {
                    val inputStream = contentResolver.openInputStream(selectedImageUri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    imageToUpload.setImageBitmap(bitmap)
                    imageToUpload.tag = "userSet"
                }
            }
        }

        // POPUP MENU para decidir si subir foto de galería o tomar foto
        btnUploadPic.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.menuInflater.inflate(R.menu.upload_pic_options_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_gallery -> {
                        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        imageLauncher.launch(intent)
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


        // CANCEL BUTTON
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener {
            val imageToUpload = findViewById<ImageView>(R.id.imageToUpload)
            if (imageToUpload.tag == "userSet") {
                showCancelConfirmationDialog()
            } else {
                showToast("No hay post por eliminar.")
            }
        }

        fun showToast(message: String) {
            val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
            toast.show()
        }

        // POPUP DE QUE SE COMPARTIO CON EXITO
        val uploaded_photo = layoutInflater.inflate(R.layout.post_uploaded_popup, null)


        // SHARE POST TO FACEBOOK BUTTON
        val btnShare = findViewById<Button>(R.id.btnShare)

        btnShare.setOnClickListener {
            //val editTextDescription = findViewById<TextView>(R.id.editTextDescription)
            //val descriptionText = editTextDescription.text.toString().trim()
            val imageToUpload = findViewById<ImageView>(R.id.imageToUpload)
            val drawable = imageToUpload.drawable

            if (imageToUpload.drawable == null) {
                showToast("Es necesario subir una imagen")
                return@setOnClickListener
            }

            if (imageToUpload.tag == null || imageToUpload.tag != "userSet") {
                showToast("Es necesario subir una imagen")
                return@setOnClickListener
            }

            if (imageToUpload.drawable != null && imageToUpload.drawable.constantState != resources.getDrawable(R.drawable.image_icon).constantState) {
                val bitmap: Bitmap = when (drawable) {
                    is BitmapDrawable -> drawable.bitmap
                    is VectorDrawable -> {
                        Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888).also { //se construye la imagen bitmap
                            val canvas = Canvas(it)
                            drawable.setBounds(0, 0, canvas.width, canvas.height)
                            drawable.draw(canvas)
                        }
                    }
                    else -> throw IllegalArgumentException("Unsupported drawable type")
                }

                val photo = SharePhoto.Builder() //constructor de imagen para fb
                    .setBitmap(bitmap)
                    .build()

                val content = SharePhotoContent.Builder()//constructor de imagen en post para fb
                    .addPhoto(photo)
                    .build()

                val dialog = Dialog(this)
                dialog.setContentView(R.layout.post_uploaded_popup) //se muestra popup de redireccionando a fb

                // verificamos que el usuario haya podido compartir a fb correctamente
                if (ShareDialog.canShow(SharePhotoContent::class.java)) {
                    val btnAceptar = dialog.findViewById<Button>(R.id.confirmPasswordButton)

                    btnAceptar.setOnClickListener {
                        dialog.dismiss()
                        ShareDialog.show(this@CreatingPost, content)
                        imageToUpload.setImageResource(R.drawable.image_icon) //se elimina la imagen para poder volver a upload
                        imageToUpload.tag = null //se elimina el tag para mostrar error de no hay foto correctamente
                    }
                    dialog.show()
                } else {
                    showToast("Es necesario instalar el app de Facebook para terminar")
                }
            } else {
                showToast("Ninguna imagen seleccionada")
            }
        }


    }

    // ******************** CÁMARA **********************
    // >>>>> FUNCIONES PARA PERMISOS DE USAR CAMARA:
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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageToUpload.setImageBitmap(imageBitmap)
            imageToUpload.tag = "userSet"
        }
    }

    // *****************************************************

    // FUNCIÓN PARA CANCELAR POST:
    private fun showCancelConfirmationDialog() {
        val imageToUpload = findViewById<ImageView>(R.id.imageToUpload)

        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Cancelar Post")
        alertDialog.setMessage("¿Estás seguro de que deseas cancelar este post? La descripción y la imagen se eliminarán.")
        alertDialog.setPositiveButton("Sí") { _, _ ->
            // La imagen se regresa a default
            imageToUpload.setImageResource(R.drawable.image_icon)
            imageToUpload.tag = null

            // Toast de confirmación
            val toast = Toast.makeText(this, "El post se ha eliminado correctamente.", Toast.LENGTH_SHORT)
            toast.show()
        }
        alertDialog.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    // TOAST de post compartido con éxito:
    private fun showToast(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.show()
    }
    // FUNCIÓN PARA COMPARTIR POST A FACEBOOK:
    private fun showShareConfirmationDialog() {

        // VARIABLES PARA BACK (SAM, ANA) AQUÍ SE GUARDA LO QUE EL USUSARIO PONGA DE
        // DESCRIPCIÓN Y LA FOTO:
        //val editTextDescription = findViewById<TextView>(R.id.editTextDescription)
        val imageToUpload = findViewById<ImageView>(R.id.imageToUpload)

        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Compartir en Facebook")
        alertDialog.setMessage("¿Deseas compartir este post en Facebook?")

        alertDialog.setPositiveButton("Sí") { _, _ ->
            // Aquí puedes agregar la lógica para compartir en Facebook BACKEND!!
            //editTextDescription.text = null  // Borrar el texto
            imageToUpload.setImageResource(R.drawable.image_icon) // Configurar la imagen predeterminada
            showToast("El post se ha compartido exitosamente en Facebook.")
        }

        alertDialog.setNegativeButton("No") { _, _ ->
            // Aquí puedes agregar la lógica para eliminar el post BACKEND!!

            //editTextDescription.text = null  // Borrar el texto
            imageToUpload.setImageResource(R.drawable.image_icon) // Configurar la imagen predeterminada
            showToast("El post se ha eliminado correctamente.")
        }

        alertDialog.show()
    }

}
