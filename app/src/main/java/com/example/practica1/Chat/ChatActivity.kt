package com.example.practica1.Chat

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.practica1.Adaptadores.AdaptadorChat
import com.example.practica1.Constantes
import com.example.practica1.Modelos.Chat
import com.example.practica1.R
import com.example.practica1.databinding.ActivityChatBinding
import com.google.common.base.Objects
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class ChatActivity : AppCompatActivity() {

    private lateinit var binding : ActivityChatBinding
    private var uid = ""
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private var miuid = ""
    private var chatRuta = ""
    private var imagenUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog (this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        uid = intent.getStringExtra("uid")!!

        miuid =  firebaseAuth.uid!!
        chatRuta = Constantes.rutaChat(uid, miuid)

        binding.adjuntarFAB.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                imagenGaleria()
            }else{
                solicitarPermisoAlmacenamiento.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        binding.enviarFAB.setOnClickListener {
            validarMensaje()
        }

        cargarInfo()

        cargarMensajes()
    }

    private fun cargarMensajes() {
        val mensajesArrayList = ArrayList<Chat>()

        val ref = FirebaseDatabase.getInstance().getReference("Chats")
        ref.child(chatRuta)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    mensajesArrayList.clear()
                    for (ds : DataSnapshot in snapshot.children){
                        try {
                            val chat = ds.getValue(Chat::class.java)
                            mensajesArrayList.add(chat!!)
                        }catch (e : Exception){

                        }
                    }
                    val adaptadorChat = AdaptadorChat(this@ChatActivity, mensajesArrayList)
                    binding.chatsRV.adapter = adaptadorChat

                    binding.chatsRV.setHasFixedSize(true)
                    var linearLayoutManager = LinearLayoutManager(this@ChatActivity)

                    linearLayoutManager.stackFromEnd = true
                    binding.chatsRV.layoutManager = linearLayoutManager
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun validarMensaje() {
        val mensaje = binding.EtMensajeChat.text.toString().trim()
        val tiempo = Constantes.obtenerTiempoD()

        if (mensaje.isEmpty()){
            Toast.makeText(
                this,
                "Ingrese un mensaje",
                Toast.LENGTH_SHORT
            ).show()
        }else {
            enviarMensaje(Constantes.MENSAJE_TIPO_TEXTO, mensaje, tiempo)
        }
    }

    private fun cargarInfo(){
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(uid)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombres = "${snapshot.child("nombres").value}"
                    val imagenBase64 = "${snapshot.child("imagen").value}"
                    val estado = "${snapshot.child("estado").value}"

                    binding.txtEstadoChat.text = estado

                    binding.txtNombreUsuario.text = nombres
                    if (!imagenBase64.isNullOrEmpty()) {
                        try {
                            val imageBytes = Base64.decode(imagenBase64, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            binding.toolbarIv.setImageBitmap(bitmap)
                        } catch (e: Exception) {
                            binding.toolbarIv.setImageResource(R.drawable.perfil_usuario)
                        }
                    } else {
                        binding.toolbarIv.setImageResource(R.drawable.perfil_usuario)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun imagenGaleria (){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"

        resultadoGaleriaARL.launch(intent)
    }
    private val resultadoGaleriaARL =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ resultado->
            if (resultado.resultCode == Activity.RESULT_OK){
                val data = resultado.data
                imagenUri = data!!.data
                subirImgBase64()
            }else {
                Toast.makeText(
                    this,
                    "Cancelado",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

    private val solicitarPermisoAlmacenamiento =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){ esConcedido ->
            if (esConcedido){

            }else{
                Toast.makeText(
                    this,
                    "El permiso de almacenamiento no ha sido concedido",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun subirImgBase64(){
        progressDialog.setMessage("Subiendo imagen")
        progressDialog.show()

        val tiempo = Constantes.obtenerTiempoD()

        try {
            // Convertir la imagen seleccionada a Bitmap
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imagenUri)

            // Comprimir y convertir a Base64
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)  //QUALITY DEFINE LA CALIDAD
            val imageBytes = baos.toByteArray()
            val imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT)

            // ENiar el mensaje con la cadena Base64
            enviarMensaje(Constantes.MENSAJE_TIPO_IMAGEN, imageBase64, tiempo)

        } catch (e: Exception) {
            progressDialog.dismiss()
            Toast.makeText(this, "Error al procesar imagen: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun enviarMensaje(tipoMensaje: String, mensaje: String, tiempo: Long) {
        progressDialog.setMessage("Enviando mensaje")
        progressDialog.show()

        val refChat = FirebaseDatabase.getInstance().getReference("Chats")

        val keyId = "${refChat.push().key}"
        val hashMap = HashMap<String, Any>()

        hashMap["idMensaje"] = "${keyId}"
        hashMap["tipoMensaje"] ="${tipoMensaje}"
        hashMap["mensaje"] = "${mensaje}"
        hashMap["emisorUid"] = "${miuid}"
        hashMap["receptorUid"] = "$uid"
        hashMap["tiempo"] = tiempo

        refChat.child(chatRuta)
            .child(keyId)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                binding.EtMensajeChat.setText("")
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "No se pudo enviar el mensaje debido a ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }

    private fun actualizarEstado (estado : String){
        val ref = FirebaseDatabase.getInstance().reference.child("Usuarios").child(firebaseAuth.uid!!)

        val hashMap = HashMap<String, Any>()
        hashMap["estado"] = estado
        ref!!.updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()
        actualizarEstado("Online")
    }

    override fun onPause() {
        super.onPause()
        actualizarEstado("Offline")
    }
}





