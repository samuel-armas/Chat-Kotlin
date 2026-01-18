package com.example.practica1

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practica1.databinding.ActivityMainBinding
import com.example.practica1.fragmentos.FragmentChats
import com.example.practica1.fragmentos.FragmentPerfil
import com.example.practica1.fragmentos.FragmentUsuarios
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import android.Manifest



class MainActivity : BaseActivity() {

    private lateinit var binding : ActivityMainBinding

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        aplicarInsets(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        comprobarSesion()

        //FRAGMENTO POR DEFECTO
        verFragmentoPerfil()
        obtenerTokenFCM()
        solicitarPermisoNotificaciones()
        binding.bottomNV.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.item_perfil->{
                    //Visualizar el fragmento Perfil
                    verFragmentoPerfil()
                    true
                }
                R.id.item_usuarios->{
                    //VISUALIZAR EL FRAGMENTO USUARIOS
                    verFragmentoUsuarios()
                    true
                }
                R.id.item_chats->{
                    //Visualizar el fragmento Chats
                    verFragmentoChats()
                    true
                }
                else-> {
                    false
                }

            }
        }

    }
    //PODRÍA CAUSAR PROBLEMAS
    private fun comprobarSesion() {
        if (firebaseAuth.currentUser == null){
            startActivity(Intent(applicationContext, OpcionesLoginActivity::class.java))
            finishAffinity()

        }else{
            agregarToken()
            solicitarPermisoNotificaciones()
        }

    }

    private fun verFragmentoPerfil(){
        binding.tvTitulo.text = "Perfil"

        val fragment = FragmentPerfil()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentoFL.id, fragment, "Fragment Perfil")
        fragmentTransaction.commit()

    }
    private fun verFragmentoUsuarios(){
        binding.tvTitulo.text = "Usuarios"

        val fragment = FragmentUsuarios()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentoFL.id, fragment, "Fragment Usuarios")
        fragmentTransaction.commit()
    }
    private fun verFragmentoChats(){
        binding.tvTitulo.text = "Chats"

        val fragment = FragmentChats()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentoFL.id, fragment, "Fragment Chats")
        fragmentTransaction.commit()

    }
    private fun actualizarEstado(estado: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val ref = FirebaseDatabase.getInstance()
                .reference.child("Usuarios").child(user.uid)

            val hashMap = HashMap<String, Any>()
            hashMap["estado"] = estado
            ref.updateChildren(hashMap)
        } else {
            // No hay usuario logueado, no intentes actualizar
            Log.d("MainActivity", "Usuario es null, no se actualiza estado")
        }
    }
    override fun onResume() {
        super.onResume()
        actualizarEstado("Online")
    }
    override fun onPause() {
        super.onPause()
        actualizarEstado("Offline")
    }

    //AQUI ESTA EN DUDA, ESTA FUNCION YA AGREGA EL TOKEN (VERIFICAR)
    private fun agregarToken (){
        val miuid = "${firebaseAuth.uid}"
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { fcmToken ->
                val hashMap = HashMap<String, Any>()
                hashMap["fcmToken"] = "${fcmToken}"
                val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
                ref.child(miuid)
                    .updateChildren(hashMap)
                    .addOnSuccessListener {
                    /*EL TOKEN SE AGREGÓ CORRECTAMETE*/
                    }
                    .addOnFailureListener {e->
                        Toast.makeText(
                            this,
                            "${e.message}",
                            LENGTH_SHORT
                        ).show()
                    }
            }
            .addOnFailureListener { e->
                Toast.makeText(
                    this,
                    "${e.message}",
                    LENGTH_SHORT
                ).show()
            }
    }
    private fun solicitarPermisoNotificaciones (){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_DENIED){
                concederPermiso.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    private val concederPermiso = registerForActivityResult(ActivityResultContracts.RequestPermission()){esConcedido->
        //EL PERMISO SE HA CONCEDIDO

    }


    private fun obtenerTokenFCM() {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                val uid = FirebaseAuth.getInstance().uid ?: return@addOnSuccessListener

                FirebaseDatabase.getInstance()
                    .getReference("Usuarios")
                    .child(uid)
                    .child("fcmToken")
                    .setValue(token)
            }
    }
}


