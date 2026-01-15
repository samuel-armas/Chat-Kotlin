package com.example.practica1

import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.practica1.databinding.ActivityEdtarInformacionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import java.io.ByteArrayOutputStream

class EdtarInformacion : AppCompatActivity() {

    private lateinit var binding: ActivityEdtarInformacionBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    // Selector de imagen
    private val seleccionarImagen = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)

            // Convertir a Base64
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
            val imageBytes = baos.toByteArray()
            val imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT)

            // Guardar en Firebase Database
            val uid = firebaseAuth.uid
            val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
            ref.child(uid!!).child("imagen").setValue(imageBase64)
                .addOnSuccessListener {
                    Toast.makeText(this, "Foto actualizada", Toast.LENGTH_SHORT).show()
                    binding.ivPerfil.setImageBitmap(bitmap) // mostrar inmediatamente
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEdtarInformacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        cargarInformacion()

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnActualizar.setOnClickListener {
            validarInformacion()
        }

        binding.IvEditarImg.setOnClickListener {
            seleccionarImagen.launch("image/*")
        }
    }

    private var nombres = ""
    private fun validarInformacion() {
        nombres = binding.etNombres.text.toString().trim()

        if (nombres.isEmpty()) {
            binding.etNombres.error = "Ingrese nombres"
            binding.etNombres.requestFocus()
        } else {
            actualizarInfo()
        }
    }

    private fun actualizarInfo() {
        progressDialog.setMessage("Actualizando información")
        progressDialog.show()

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["nombres"] = nombres

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Se actualizó su información", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cargarInformacion() {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombres = snapshot.child("nombres").value?.toString() ?: ""
                    val imagenBase64 = snapshot.child("imagen").value?.toString()

                    binding.etNombres.setText(nombres)

                    if (!imagenBase64.isNullOrEmpty()) {
                        try {
                            val imageBytes = Base64.decode(imagenBase64, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            binding.ivPerfil.setImageBitmap(bitmap)
                        } catch (e: Exception) {
                            Toast.makeText(applicationContext, "Error al cargar imagen", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        binding.ivPerfil.setImageResource(R.drawable.ic_img_perfil)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}

