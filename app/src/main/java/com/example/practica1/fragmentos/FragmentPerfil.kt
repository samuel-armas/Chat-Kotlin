package com.example.practica1.fragmentos

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.practica1.Constantes
import com.example.practica1.EdtarInformacion
import com.example.practica1.OpcionesLoginActivity
import com.example.practica1.R
import com.example.practica1.databinding.FragmentPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.util.Base64
import android.graphics.BitmapFactory
import com.example.practica1.CambiarPassword
import com.google.firebase.database.core.Context

class FragmentPerfil : Fragment() {

    private lateinit var binding : FragmentPerfilBinding
    private lateinit var mContext: android.content.Context
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onAttach(context: android.content.Context) {
        mContext = context
        super.onAttach(context)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPerfilBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        cargarInformacion()

        binding.btnActualizarInfo.setOnClickListener {
            startActivity(Intent(mContext, EdtarInformacion::class.java))
        }

        binding.btnCambiarPass.setOnClickListener {
            startActivity(Intent(mContext, CambiarPassword::class.java))
        }

        binding.btnCerrarsesion.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(mContext, OpcionesLoginActivity::class.java))
            activity?.finishAffinity()
        }
    }

    private fun cargarInformacion() {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombres = "${snapshot.child("nombres").value}"
                    val email = "${snapshot.child("email").value}"
                    val proveedor = "${snapshot.child("proveedor").value}"
                    var  t_registro = "${snapshot.child("tiempoR").value}"
                    val imagenBase64 = snapshot.child("imagen").value?.toString()

                    if (t_registro == null){
                        t_registro = "0"
                    }
                    //Conversion a fecha
                    val fecha = Constantes.formatoFecha(t_registro.toLong())

                    //SETEAR LA INFORMACION EN LAS VISTAS
                    binding.tvNombres.text = nombres
                    binding.tvEmail.text = email
                    binding.tvProveedor.text = proveedor
                    binding.tvTRegistro.text=fecha

                    //setear la imagen en el TV
                    if (!imagenBase64.isNullOrEmpty()) {
                        try {
                            val imageBytes = Base64.decode(imagenBase64, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            binding.ivPerfil.setImageBitmap(bitmap)
                        } catch (e: Exception) {
                            Toast.makeText(mContext, "Error al cargar imagen", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        binding.ivPerfil.setImageResource(R.drawable.ic_img_perfil)
                    }

                    if (proveedor == "Email"){
                        binding.btnCambiarPass.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })



    }
}